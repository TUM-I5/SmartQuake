package de.ferienakademie.smartquake.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewTreeObserver;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final CanvasView canvasView = (CanvasView) findViewById(R.id.shape);

        ViewTreeObserver viewTreeObserver = canvasView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    canvasView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    double width = canvasView.getWidth();
                    double height = canvasView.getHeight();
                    double middle = canvasView.getWidth() * 0.25f;

                    Structure structure = new Structure();

                    Node n1 = new Node(middle, height);
                    Node n2 = new Node(width - middle, height);
                    Node n3 = new Node(width - middle, height - middle);
                    Node n4 = new Node(middle, height - middle);
                    Node n5 = new Node(2 * middle, height - 2 * middle);

                    Beam b1 = new Beam(n1, n2);
                    Beam b2 = new Beam(n2, n3);
                    Beam b3 = new Beam(n3, n4);
                    Beam b4 = new Beam(n4, n1);
                    Beam b5 = new Beam(n4, n5);
                    Beam b6 = new Beam(n5, n3);

                    structure.addNodes(n1, n2, n3, n4, n5);
                    structure.addBeams(b1, b2, b3, b4, b5, b6);

                    canvasView.drawStructure(structure);
                }
            });
        }
    }

}
