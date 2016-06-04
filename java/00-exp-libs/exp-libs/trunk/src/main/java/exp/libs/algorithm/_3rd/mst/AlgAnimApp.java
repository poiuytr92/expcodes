package exp.libs.algorithm._3rd.mst;
/* AlgAnimApp.java */

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.io.IOException;
import java.net.URL;

public class AlgAnimApp extends Applet {
    /** serialVersionUID */
	private static final long serialVersionUID = 8915250167190236023L;
	static String fn_label = "filename";
    static String button_label = "buttonname";
    URL homeURL, sourceURL;
    Button start_button;

    public void init() {
	setBackground( Color.white );
        String file_name = this.getParameter(fn_label); 
        homeURL = getCodeBase();
        try {
            sourceURL = new URL( homeURL, file_name );
        } catch( IOException e ) {
            System.out.println("URL error " + file_name );
        }

        start_button = new Button(getParameter(button_label));
        add(start_button);

        validate();
    } // init()

    public boolean action(Event e, Object arg) {
        Object target = e.target;
 
        if (target == start_button) {
            start_button.disable();
            new AlgAnimFrame(this, sourceURL);
            return true;
        }
        return false;
    } // action()

} // class AlgAnimApp
