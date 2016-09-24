package de.ferienakademie.smartquake.excitation;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by David Schneller on 23.09.2016.
 */
public class StructureIO {

    public static void writeStructure(Writer stream, Structure structure) throws IOException {
        JsonWriter writer = new JsonWriter(stream);
        writer.beginObject();
        writer.name("nodes").beginArray();
        for (Node node : structure.getNodes())
        {
            writeNode(writer, node);
        }
        writer.endArray();
        writer.name("beams").beginArray();
        for (Beam beam : structure.getBeams())
        {
            writer.beginObject();
            writer.name("start");
            int indexStart = structure.getNodes().indexOf(beam.getStartNode());
            writer.value(indexStart);
            writer.name("end");
            int indexEnd = structure.getNodes().indexOf(beam.getEndNode());
            writer.value(indexEnd);
            writer.endObject();
        }
        writer.endArray();
        writer.name("conDOF").beginArray();
        for (Integer i : structure.getConDOF()) {
            writer.value(i);
        }
        writer.endArray();
        writer.endObject();
        writer.flush();
        stream.close();
    }

    private static void writeNode(JsonWriter writer, Node node) throws IOException {
        writer.beginObject();
        writer.name("x").value(node.getInitX());
        writer.name("y").value(node.getInitY());

        writer.name("currRot").beginArray();
        for (Double d : node.getCurrROT()) {
            writer.value(d);
        }
        writer.endArray();

        writer.name("DOF").beginArray();
        for (Integer i : node.getDOF()) {
            writer.value(i);
        }
        writer.endArray();

        writer.endObject();
    }

    private static Node parseNode(JsonReader reader) throws IOException {
        double x = Double.NaN, y = Double.NaN;
        LinkedList<Integer> DOF = new LinkedList<>();
        LinkedList<Double> currRot = new LinkedList<>();
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT)
        {
            String name = reader.nextName();
            switch(name)
            {
                case "x":
                    x = reader.nextDouble();
                    break;
                case "y":
                    y = reader.nextDouble();
                    break;
                case "currRot":
                    reader.beginArray();
                    while(reader.peek() != JsonToken.END_ARRAY){
                        currRot.add(reader.nextDouble());
                    }
                    reader.endArray();
                    break;
                case "DOF":
                    reader.beginArray();
                    while(reader.peek() != JsonToken.END_ARRAY){
                        DOF.add(reader.nextInt());
                    }
                    reader.endArray();
                    break;
            }
        }
        reader.endObject();

        if (Double.isNaN(x) || Double.isNaN(y))
        {
            throw new IOException("Malformed file format.");
        }

        return new Node(x, y, DOF, currRot);
    }

    private static List<Node> parseNodes(JsonReader reader) throws IOException {
        ArrayList<Node> nodes = new ArrayList<>();
        reader.beginArray();
        while (reader.peek() != JsonToken.END_ARRAY)
        {
            nodes.add(parseNode(reader));
        }
        reader.endArray();
        return nodes;
    }

    private static TemporaryBeam parseBeam(JsonReader reader) throws IOException {
        int start = -1, end = -1;

        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            String name = reader.nextName();
            switch (name) {
                case "start":
                    start = reader.nextInt();
                    break;
                case "end":
                    end = reader.nextInt();
                    break;
            }
        }
        reader.endObject();

        if (start == -1 || end == -1) {
            throw new IOException("Malformed file format.");
        }

        return new TemporaryBeam(start, end);
    }

    private static List<TemporaryBeam> parseBeams(JsonReader reader) throws IOException {
        ArrayList<TemporaryBeam> beams = new ArrayList<>();
        reader.beginArray();
        while (reader.peek() != JsonToken.END_ARRAY)
        {
            beams.add(parseBeam(reader));
        }
        reader.endArray();
        return beams;
    }

    public static Structure readStructure(Reader stream) throws IOException {
        JsonReader reader = new JsonReader(stream);
        reader.beginObject();

        List<TemporaryBeam> tempBeams = null;
        List<Node> nodes = null;
        while (reader.peek() != JsonToken.END_OBJECT) {
            String name = reader.nextName();
            switch (name) {
                case "nodes":
                    nodes = parseNodes(reader);
                    break;
                case "beams":
                    tempBeams = parseBeams(reader);
                    break;
            }
        }

        if (tempBeams == null || nodes == null) {
            throw new IOException("Malformed file format.");
        }

        List<Beam> beams = new ArrayList<>();
        for (TemporaryBeam tbeam : tempBeams) {
            Beam b = new Beam(nodes.get(tbeam.start), nodes.get(tbeam.end));
            beams.add(b);
        }
        stream.close();
        //TODO store conDofs!!
        return new Structure(nodes, beams, new LinkedList<Integer>()); //? whatever...
    }

    private static class TemporaryBeam {
        int start;
        int end;

        public TemporaryBeam(int start, int end)
        {
            this.start = start;
            this.end = end;
        }
    }
}
