package com.italoweb.gestorfinan.components;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Span;
import org.zkoss.zul.Style;

import com.italoweb.gestorfinan.util.ComponentsUtil;

public class Switch extends Div {
    private static final long serialVersionUID = -722212510935051634L;

	public final static String ON_TOGGLE = "onSwitch";

    private boolean checked;
    private boolean disable = false;
    private Span span_toggle;

    public Switch() {
        this.checked = false;
        this.init();
    }

    public Switch(boolean checked) {
        this.checked = checked;
        this.init();
    }

    private void init() {
        this.addStyle();
        this.span_toggle = ComponentsUtil.getSpan("", null);
        this.appendChild(this.span_toggle);
        this.updateComponent();
        this.span_toggle.addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {
            private static final long serialVersionUID = 1611279775001505076L;
            public void onEvent(Event event) throws Exception {
                if(!disable) {
                    checked = !checked;
                    updateComponent();
                    echoEvent();
                }
            }
        });
    }

    private void addStyle() {
        StringBuilder content = new StringBuilder("");
        content.append(".infodigg_toggle{");
        content.append("	font-size: 25px;");
        content.append(" 	cursor: pointer;");
        content.append("}");
        content.append(".infodigg_toggle_active{");
        content.append("    color: #5cb85c;");
        content.append("}");
        Style style = new Style();
        style.setContent(content.toString());
        this.appendChild(style);
    }

    private void updateComponent() {
        String sclass = "infodigg_toggle";
        if(this.checked) {
            sclass += " fa fa-toggle-on infodigg_toggle_active";
        }else {
            sclass += " fa fa-toggle-off";
        }
        this.span_toggle.setClass(sclass);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        this.updateComponent();
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    private void echoEvent(){
        Events.echoEvent(Switch.ON_TOGGLE, this, this.checked);
    }
}
