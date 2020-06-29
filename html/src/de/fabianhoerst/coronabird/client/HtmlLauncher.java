package de.fabianhoerst.coronabird.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

import java.awt.Dimension;

import de.fabianhoerst.coronabird.CoronaBird;

public class HtmlLauncher extends GwtApplication {

        // USE THIS CODE FOR A FIXED SIZE APPLICATION
        // Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        //@Override
        //public GwtApplicationConfiguration getConfig () {
        //        return new GwtApplicationConfiguration(1440, 2560);
        //}

        //Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        // END CODE FOR FIXED SIZE APPLICATION

        // UNCOMMENT THIS CODE FOR A RESIZABLE APPLICATION
        // PADDING is to avoid scrolling in iframes, set to 20 if you have problems
         private static final int PADDING = 0;
         private GwtApplicationConfiguration cfg;

         @Override
         public GwtApplicationConfiguration getConfig() {
             int h = Window.getClientHeight() - PADDING;
             int w = 9*h/16;
             cfg = new GwtApplicationConfiguration(w, h);
             Window.enableScrolling(false);
             Window.setMargin("0");
             Window.addResizeHandler(new ResizeListener());
             cfg.preferFlash = false;
             return cfg;
        }

        class ResizeListener implements ResizeHandler {
             @Override
             public void onResize(ResizeEvent event) {
                 int height = event.getHeight() - PADDING;
                 int width = height*9/16;
                 getRootPanel().setWidth("" + width + "px");
                 getRootPanel().setHeight("" + height + "px");
                 getApplicationListener().resize(width, height);
                 Gdx.graphics.setWindowedMode(width, height);
             }
         }
        // END OF CODE FOR RESIZABLE APPLICATION

        @Override
        public ApplicationListener createApplicationListener () {
                return new CoronaBird();
        }
}