package autocadDrawingChecker.start;

import autocadDrawingChecker.grading.GradingReport;
import autocadDrawingChecker.grading.LineLength;
import autocadDrawingChecker.gui.AppWindow;
import autocadDrawingChecker.logging.Logger;

/**
 * Main servers as the starting point for the
 * application. Future versions may add support
 * for running the application from the command
 * line, with no GUI.
 * 
 * @author Matt Crow
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application app = new Application();
        GradingReport report = app
            .setSrcPath("C:\\Users\\Matt\\Desktop\\AutoCAD Drawing Checker\\sample files to work with")
            .setCmpPaths("C:\\Users\\Matt\\Desktop\\AutoCAD Drawing Checker\\sample files to work with")
            .setCriteria(new LineLength())
            .grade();
        Logger.log(report.toString());
        
        //new AppWindow();
    }
}
