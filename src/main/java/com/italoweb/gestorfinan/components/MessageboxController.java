package com.italoweb.gestorfinan.components;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.impl.MessageboxDlg;

public class MessageboxController extends MessageboxDlg{

	private static final long serialVersionUID = -4779454367413936937L;
	
	@Override
	public void setButtons(Messagebox.Button[] buttons, String[] btnLabels) {
		super.setButtons(buttons, btnLabels);
		Component parent = getFellowIfAny("buttons");
		if(parent != null) {
			parent.getChildren().clear();
			for(int j = 0; j < buttons.length; ++j) {
				final Button mbtn = new Button();
				mbtn.setButton(buttons[j], btnLabels != null && j < btnLabels.length ? btnLabels[j] : null);
				switch (buttons[j].id) {
				case Messagebox.ABORT:
					mbtn.setSclass("btn btn-warning");
					break;
				case Messagebox.CANCEL:
				case Messagebox.IGNORE:
				case Messagebox.NO:
				case Messagebox.RETRY:
					mbtn.setSclass("btn btn-info");
					break;
				case Messagebox.OK:{
					switch ((String)this.getAttribute("window.icon")) {
					case Messagebox.ERROR:
						mbtn.setSclass("btn btn-danger");
						break;
					case Messagebox.EXCLAMATION:
						mbtn.setSclass("btn btn-warning");
						break;
					default:
						mbtn.setSclass("btn btn-primary");
						break;
					}
					break;
				}
				case Messagebox.YES:
					mbtn.setSclass("btn btn-success");
					break;
				}
				mbtn.setZclass("none");
				mbtn.setAutodisable("self");
				parent.appendChild(mbtn);
			}
		}
		Component icon = getFellowIfAny("span_icon");
		if(icon != null) {
			switch ((String)this.getAttribute("window.icon")) {
			case Messagebox.QUESTION:
				((Label)icon).setSclass("bi bi-question-circle icon-question");
				break;
			case Messagebox.INFORMATION:
				((Label)icon).setSclass("bi bi-info-circle icon-information");
				break;
			case Messagebox.ERROR:
				((Label)icon).setSclass("bi bi-x-circle icon-error");
				break;
			case Messagebox.EXCLAMATION:
				((Label)icon).setValue("\uFE57");
				((Label)icon).setSclass("icon-exclamation");
				break;
			}
		}
	}
	
}