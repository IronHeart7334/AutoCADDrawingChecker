package autocadDrawingChecker.gui.chooseCriteria;

import autocadDrawingChecker.data.core.DataSet;
import autocadDrawingChecker.grading.criteria.AbstractGradingCriteria;
import autocadDrawingChecker.gui.AbstractPage;
import autocadDrawingChecker.start.DrawingCheckerData;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Matt
 */
public class ChooseCriteriaPage extends AbstractPage {
    private final CriteriaSelectionList critList;
    
    public ChooseCriteriaPage() {
        super("Choose criteria to grade on");
        setLayout(new GridLayout(1, 1));
        
        critList = new CriteriaSelectionList();
        add(critList);
    }
    
    public final void setCriteriaSelected(AbstractGradingCriteria<? extends DataSet> crit, boolean isSelected){
        critList.setCriteriaSelected(crit, isSelected);
    }
    
    public final List<AbstractGradingCriteria<? extends DataSet>> getSelectedCriteria(){
        return critList.getSelectedCriteria();
    }

    @Override
    protected boolean checkIfReadyForNextPage() {
        boolean ready = true || !critList.getSelectedCriteria().isEmpty();
        if(!ready){
            JOptionPane.showMessageDialog(this, "Please select at least 1 criteria to grade on");
        }
        return ready;
    }

    @Override
    protected void dataUpdated(DrawingCheckerData newData) {
        HashMap<AbstractGradingCriteria<? extends DataSet>, Boolean> critToIsSel = newData.getGradableCriteriaToIsSelected();
        critList.setCriteriaOptions(new LinkedList<>(critToIsSel.keySet()));
        critToIsSel.entrySet().forEach((entry)->{
            critList.setCriteriaSelected(entry.getKey(), entry.getValue());
        });
    }
}
