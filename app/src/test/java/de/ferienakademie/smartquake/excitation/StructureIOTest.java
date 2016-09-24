package de.ferienakademie.smartquake.excitation;

import org.junit.Test;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;

import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.model.StructureFactory;

import static org.junit.Assert.assertTrue;

/**
 * Created by simon on 23.09.16.
 */
public class StructureIOTest {

    @Test
    public void testParsing() {
        Structure test1 = StructureFactory.getSimpleHouse(10, 10);
        CharArrayWriter writer = new CharArrayWriter();
        try {
            StructureIO.writeStructure(writer, test1);
            char[] charArray = writer.toCharArray();
            CharArrayReader reader = new CharArrayReader(charArray);
            Structure structure = StructureIO.readStructure(reader);
            assertTrue(structure.getNodes().containsAll(structure.getNodes()));
            assertTrue(structure.getBeams().containsAll(structure.getBeams()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StructureFactory.getSimpleEiffelTower(20, 33);
    }
}