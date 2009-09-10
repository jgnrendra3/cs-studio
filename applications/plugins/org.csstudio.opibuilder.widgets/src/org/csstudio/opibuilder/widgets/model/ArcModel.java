package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;


/**The widget model 
 * @author Xihui Chen
 *
 */
public class ArcModel extends AbstractShapeModel {
	
	
	public final String ID = "org.csstudio.opibuilder.widgets.arc";
	
	public static final String PROP_FILL = "fill";//$NON-NLS-1$

	public static final String PROP_START_ANGLE = "angle.start";//$NON-NLS-1$
	
	
	public static final String PROP_TOTAL_ANGLE = "angle.total";//$NON-NLS-1$
	
	
	public ArcModel() {
		setLineWidth(1);
	}
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		removeProperty(PROP_FILL_LEVEL);
		removeProperty(PROP_HORIZONTAL_FILL);
		removeProperty(PROP_TRANSPARENT);
		addProperty(new BooleanProperty(PROP_FILL, "Fill", 
				WidgetPropertyCategory.Display, false));
		addProperty(new IntegerProperty(PROP_START_ANGLE, "Start Angle", 
				WidgetPropertyCategory.Display, 0, 0, 360));
		addProperty(new IntegerProperty(PROP_TOTAL_ANGLE, "Total Angle", 
				WidgetPropertyCategory.Display, 90, 0, 360));
	
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	
	public boolean isFill(){
		return (Boolean)getCastedPropertyValue(PROP_FILL);
	}
	
	public void setFill(boolean value){
		setPropertyValue(PROP_FILL, value);
	}
	
	public int getStartAngle(){
		return (Integer)getCastedPropertyValue(PROP_START_ANGLE);
	}
	
	public void setStartAngle(int angle){
		setPropertyValue(PROP_START_ANGLE, angle);
	}
	
	public int getTotalAngle(){
		return (Integer)getCastedPropertyValue(PROP_TOTAL_ANGLE);
	}
	
	public void setTotalAngle(int angle){
		setPropertyValue(PROP_TOTAL_ANGLE, angle);
	}
	
	
	
}
