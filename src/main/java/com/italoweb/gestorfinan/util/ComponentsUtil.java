package com.italoweb.gestorfinan.util;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.A;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Doublespinner;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Rating;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Span;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Style;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Treefoot;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.components.Switch;

public class ComponentsUtil implements Serializable {

	private static final long serialVersionUID = -8913243139106458885L;

	public static void connectVariablesController(IdSpace controller) {
		// Connect fellows IDSpaces first
		for (Component fellow : controller.getFellows()) {
			if (fellow instanceof IdSpace) {
				ConventionWires.wireVariables(fellow, (Object) controller);
			}
		}

		// Connect variables controller
		ConventionWires.wireVariables((Component) controller, (Object) controller);
	}

	// ********************************************COMPONENTS

	public static Textbox getTextbox(String sclass, String placeholder, int rows, boolean readonly, String value) {
		Textbox textbox = new Textbox(value);
		textbox.setZclass("none");
		if (StringUtils.isNotBlank(sclass)) {
			textbox.setClass("form-control " + sclass);
		} else {
			textbox.setClass("form-control");
		}
		if (rows > 1) {
			textbox.setMultiline(true);
			textbox.setRows(rows);
			textbox.setStyle("resize:none");
		}
		if (StringUtils.isNotBlank(placeholder)) {
			textbox.setPlaceholder(placeholder);
		}
		textbox.setReadonly(readonly);
		return textbox;
	}

	public static Textbox getTextbox(String placeholder, int rows, boolean readonly, String value) {
		return getTextbox(null, placeholder, rows, readonly, value);
	}

	public static Textbox getTextbox(String sclass, String placeholder, int rows, boolean readonly) {
		return getTextbox(sclass, placeholder, rows, readonly, "");
	}

	public static Textbox getTextbox(String placeholder, int rows) {
		return getTextbox(null, placeholder, rows, false, "");
	}

	public static Textbox getTextbox(String placeholder, int rows, boolean readonly) {
		return getTextbox(placeholder, rows, readonly, "");
	}

	public static Intbox getIntbox(String placeholder, String sclass) {
		Intbox intbox = new Intbox();
		intbox.setClass("form-control");
		if (StringUtils.isNotBlank(placeholder)) {
			intbox.setPlaceholder(placeholder);
		}
		if (StringUtils.isNotBlank(sclass))
			intbox.setClass(sclass);
		return intbox;
	}

	public static Longbox getLongbox(String placeholder, String format) {
		return getLongbox(null, placeholder, format, 0);
	}

	public static Longbox getLongbox(Long value, String placeholder, String format, int hflex) {
		Longbox longbox = new Longbox();
		if(value != null) {
			longbox.setValue(value);
		}
		longbox.setClass("form-control");
		if (StringUtils.isNotBlank(placeholder)) {
			longbox.setPlaceholder(placeholder);
		}
		if (StringUtils.isNotBlank(format)) {
			longbox.setFormat(format);// PARA SOPORTAR EL 0 se debe poner este format "#,##0";
		}
		if(hflex > 0) {
			longbox.setHflex(hflex+"");
		}
		return longbox;
	}

	public static Doublebox getDoublebox(String placeholder, String format) {
		return getDoublebox(null, placeholder, format, 0);
	}

	public static Doublebox getDoublebox(Double value, String placeholder, String format, int hflex) {
		Doublebox doublebox = new Doublebox(value);
		doublebox.setClass("form-control");
		if (StringUtils.isNotBlank(placeholder)) {
			doublebox.setPlaceholder(placeholder);
		}
		if (StringUtils.isNotBlank(format)) {
			doublebox.setFormat(format);// Ejemplo 2 decimales este format ,###.##;
		}
		if(hflex > 0) {
			doublebox.setHflex(hflex+"");
		}
		return doublebox;
	}

	public static Component getAuxheader(String label, String align, int colspan) {
		Auxheader auxheader = new Auxheader();
		if (StringUtils.isNotBlank(label)) {
			auxheader.setLabel(label);
		}
		if (StringUtils.isNotBlank(align)) {
			auxheader.setAlign(align);
		}
		if (colspan > 1) {
			auxheader.setColspan(colspan);
		}
		return auxheader;
	}

	public static Listbox getListbox(String emptyMessage, int rows, String tags) {
		Listbox listbox = new Listbox();
		listbox.setWidth("100%");
		Listhead listhead = new Listhead();
		listbox.appendChild(listhead);
		if (StringUtils.isNotBlank(emptyMessage)) {
			listbox.setEmptyMessage(emptyMessage);
		}
		if (rows > 0) {
			listbox.setRows(rows);
		}
		if (StringUtils.isNotBlank(tags)) {
			listbox.setNonselectableTags(tags);
		}
		return listbox;
	}

	public static Listbox getListbox(String emptyMessage, boolean withHead) {
		Listbox listbox = new Listbox();
		listbox.setWidth("100%");
		if(withHead) {
			Listhead listhead = new Listhead();
			listbox.appendChild(listhead);
		}
		return listbox;
	}

	public static Listbox getListbox(String emptyMessage, String height, boolean checkmark, boolean multiple) {
		Listbox listbox = getListbox(emptyMessage, 0, null);
		if (StringUtils.isNotBlank(height)) {
			listbox.setHeight(height);
		}
		listbox.setCheckmark(checkmark);
		listbox.setMultiple(multiple);
		return listbox;
	}

	public static Listheader getListheader(String label, String width, String align) {
		return getListheader(label, width, align, null);
	}

	public static Listheader getListheader(String label, String width, String align, Component child) {
		Listheader listheader = new Listheader();
		if (StringUtils.isNotBlank(label)) {
			listheader.setLabel(label);
		}
		if (StringUtils.isNotBlank(width)) {
			listheader.setWidth(width);
		}
		if (StringUtils.isNotBlank(align)) {
			listheader.setAlign(align);
		}
		if (child != null) {
			listheader.appendChild(child);
		}
		return listheader;
	}

	public static Listitem getListitem(String label, String tooltiptext, Object value) {
		Listitem listitem = new Listitem();
		if (StringUtils.isNotBlank(label))
			listitem.setLabel(label);
		if (StringUtils.isNotBlank(tooltiptext))
			listitem.setTooltiptext(tooltiptext);
		listitem.setValue(value);
		return listitem;
	}

	public static Listitem getListitemWithCells(Object value, String tooltiptext, Listcell... cells) {
		Listitem listitem = new Listitem();
		if (StringUtils.isNotBlank(tooltiptext))
			listitem.setTooltiptext(tooltiptext);
		listitem.setValue(value);

		for (Listcell cell : cells) {
			listitem.appendChild(cell);
		}

		return listitem;
	}

	public static Listcell getListcell(String label, Component child) {
		Listcell listcell = new Listcell();
		if (StringUtils.isNotBlank(label))
			listcell.setLabel(label);
		if (child != null)
			listcell.appendChild(child);
		return listcell;
	}

	public static Listcell getListcell(String label, Component child, String sclass) {
		Listcell listcell = new Listcell();
		if (StringUtils.isNotBlank(label))
			listcell.setLabel(label);
		if (StringUtils.isNotBlank(sclass))
			listcell.setSclass(sclass);
		if (child != null)
			listcell.appendChild(child);
		return listcell;
	}

	public static Listcell getListcell(String label, Component child, int span) {
		Listcell listcell = getListcell(label, child);
		if (span > 1) {
			listcell.setSpan(span);
		}
		return listcell;
	}

	public static Listfooter getListfooter(String label, int span, String align, Component child) {
		Listfooter listfooter = new Listfooter();
		if (StringUtils.isNotBlank(label))
			listfooter.setLabel(label);
		if (span > 1)
			listfooter.setSpan(span);
		if (StringUtils.isNotBlank(align))
			listfooter.setAlign(align);
		if (child != null)
			listfooter.appendChild(child);
		return listfooter;
	}

	public static Hlayout getHlayoutFullWidth(String spacing, String valign, Component... components) {
		Hlayout result = getHlayout(spacing, valign, components);
		result.setWidth("100%");
		return result;
	}

	public static Hlayout getHlayout(String spacing, String valign, Component... components) {
		Hlayout hlayout = new Hlayout();
		if (StringUtils.isNotBlank(spacing)) {
			hlayout.setSpacing(spacing);
		}
		if (StringUtils.isNotBlank(valign)) {
			hlayout.setValign(valign);
		}
		if (components != null) {
			for (Component component : components) {
				if (component != null) {
					hlayout.appendChild(component);
				}
			}
		}
		return hlayout;
	}

	public static Hlayout getHlayoutFlex(int hflex, int vflex, String spacing, String valign, Component... components) {
		Hlayout hlayout = getHlayout(spacing, valign, components);
		if (hflex > 0) {
			hlayout.setHflex(hflex + "");
		}
		if (vflex > 0) {
			hlayout.setVflex(vflex + "");
		}
		return hlayout;
	}

	public static Vlayout getVlayout(String spacing, Component... components) {
		Vlayout vlayout = new Vlayout();
		if (StringUtils.isNotBlank(spacing)) {
			vlayout.setSpacing(spacing);
		}
		if (components != null) {
			for (Component component : components) {
				if (component != null) {
					vlayout.appendChild(component);
				}
			}
		}
		return vlayout;
	}

	public static Vlayout getVlayoutFlex(int hflex, int vflex, String spacing, Component... components) {
		Vlayout vlayout = getVlayout(spacing, components);
		if (hflex > 0) {
			vlayout.setHflex(hflex + "");
		}
		if (vflex > 0) {
			vlayout.setVflex(vflex + "");
		}
		return vlayout;
	}

	public static Column getColumnGrid(String label, String align, String width) {
		Column column = new Column();
		if (StringUtils.isNotBlank(label))
			column.setLabel(label);
		if (StringUtils.isNotBlank(align))
			column.setAlign(align);
		if (StringUtils.isNotBlank(width))
			column.setWidth(width);
		return column;
	}

	public static Column getColumnGrid(String label, String align, String width, Component component) {
		Column column = getColumnGrid(label, align, width);
		if (component != null) {
			column.appendChild(component);
		}
		return column;
	}

	public static Row getRow(Component... components) {
		Row row = new Row();
		if (components != null) {
			for (Component component : components) {
				if (component != null) {
					row.appendChild(component);
				}
			}
		}
		return row;
	}

	public static Cell getCell(int colspan, Component component) {
		Cell cell = new Cell();
		if (colspan > 0)
			cell.setColspan(colspan);
		if (component != null)
			cell.appendChild(component);
		return cell;
	}

	public static Label getLabel(String value, String sclass) {
		Label label = new Label();
		if (StringUtils.isNotBlank(value)) {
			label.setValue(value);
		}
		if (StringUtils.isNotBlank(sclass)) {
			label.setClass(sclass);
		}
		return label;
	}

	public static Label getLabel(String value, String sclass, boolean multiline) {
		Label label = getLabel(value, sclass);
		label.setMultiline(multiline);
		return label;
	}

	public static Label getLabel(String value, String sclass, int hflex) {
		Label label = getLabel(value, sclass);
		if (hflex > 0) {
			label.setHflex(String.format("%s", hflex));
		}
		return label;
	}

	public static Label getLabel(String value, String tooltiptext, String sclass) {
		Label label = getLabel(value, sclass);
		if (StringUtils.isNotBlank(tooltiptext)) {
			label.setTooltiptext(tooltiptext);
		}
		return label;
	}

	public static Div getDiv(String sclass, Component... components) {
		Div div = new Div();
		if (StringUtils.isNotBlank(sclass))
			div.setClass(sclass);
		if (components != null) {
			for (Component component : components) {
				if (component != null) {
					div.appendChild(component);
				}
			}
		}
		return div;
	}

	public static Div getDiv(String sclass, String padding, Component... components) {
		Div div = new Div();
		if (StringUtils.isNotBlank(sclass))
			div.setClass(sclass);
		if (StringUtils.isNotBlank(padding))
			div.setStyle("padding:" + padding);
		if (components != null) {
			for (Component component : components) {
				if (component != null) {
					div.appendChild(component);
				}
			}
		}
		return div;
	}

	public static Div getDivFlex(int hflex, int vflex, String sclass, Component... components) {
		Div div = getDiv(sclass, components);
		if (hflex > 0) {
			div.setHflex(hflex + "");
		}
		if (vflex > 0) {
			div.setVflex(vflex + "");
		}
		return div;
	}

	public static Div getDivInputGroup(boolean reverse, Textbox textbox, Button... buttons) {
		if (textbox == null || buttons == null || buttons.length == 0) {
			return null;
		}
		Div div = getDiv("input-group");
		Span span = getSpan("input-group-btn", null);
		for (Button button : buttons) {
			if (button != null) {
				span.appendChild(button);
			}
		}
		if (reverse) {
			div.appendChild(span);
			div.appendChild(textbox);
		} else {
			div.appendChild(textbox);
			div.appendChild(span);
		}
		return div;
	}

	public static Span getSpan(String sclass) {
		return getSpan(sclass, null);
	}

	public static Span getSpan(String sclass, Component child) {
		return getSpan(sclass, child, null);
	}

	public static Span getSpan(String sclass, Component child, EventListener<? extends Event> eventListener) {
		Span span = new Span();
		if (StringUtils.isNotBlank(sclass)) {
			span.setClass(sclass);
		}
		if (child != null) {
			span.appendChild(child);
		}
		if (eventListener != null) {
			span.addEventListener(Events.ON_CLICK, eventListener);
		}
		return span;
	}

	public static Button getButton(String label, String sclass, String iconSclass) {
		return getButton(label, sclass, iconSclass, null, 0, null);
	}

	public static Button getButton(String label, String sclass, String iconSclass, String tooltiptext) {
		return getButton(label, sclass, iconSclass, tooltiptext, 0, null);
	}

	public static Button getButton(String label, String sclass, String iconSclass, EventListener<? extends Event> eventListener) {
		return getButton(label, sclass, iconSclass, null, 0, eventListener);
	}

	public static Button getButton(String label, String sclass, String iconSclass, String tooltiptext, EventListener<? extends Event> eventListener) {
		return getButton(label, sclass, iconSclass, tooltiptext, 0, eventListener);
	}

	public static Button getButton(String label, String sclass, String iconSclass, String tooltiptext, int hflex, EventListener<? extends Event> eventListener) {
		Button button = new Button();
		button.setZclass("none");
		button.setAutodisable("self");
		if (StringUtils.isNotBlank(label)) {
			button.setLabel(label);
		}
		if (StringUtils.isNotBlank(sclass)) {
			button.setClass(sclass);
		}
		if (StringUtils.isNotBlank(iconSclass)) {
			button.setIconSclass(iconSclass);
		}
		if (StringUtils.isNotBlank(tooltiptext)) {
			button.setTooltiptext(tooltiptext);
		}
		if(hflex > 0){
			button.setHflex(hflex+"");
		}
		if (eventListener != null) {
			button.addEventListener(Events.ON_CLICK, eventListener);
		}
		return button;
	}

	public static A getA(String label, String sclass, String iconSclass) {
		return getA(label, sclass, iconSclass, null);
	}

	public static A getA(String label, String sclass, String iconSclass, String href) {
		A a = new A();
		a.setZclass("none");
		a.setAutodisable("self");
		if (StringUtils.isNotBlank(label)) {
			a.setLabel(label);
		}
		if (StringUtils.isNotBlank(sclass)) {
			a.setClass(sclass);
		}
		if (StringUtils.isNotBlank(iconSclass)) {
			a.setIconSclass(iconSclass);
		}
		if (StringUtils.isNotBlank(href)) {
			a.setHref(href);
			a.setTarget("_blank");
		}
		return a;
	}

	public static Grid getGrid(boolean borderLess) {
		return getGrid(borderLess, null);
	}

	public static Grid getGrid(boolean borderLess, String height) {
		Grid grid = new Grid();
		grid.appendChild(new Columns());
		grid.appendChild(new Rows());
		if (borderLess) {
			grid.setClass("z-grid-border-less");
		}
		if (StringUtils.isNotBlank(height)) {
			grid.setHeight(height);
		}
		return grid;
	}

	public static Separator getSeparatorFlex(String orient, int hflex, int vflex) {
		Separator separator = new Separator(orient);
		if (hflex > 0) {
			separator.setHflex(hflex + "");
		}
		if (vflex > 0) {
			separator.setVflex(vflex + "");
		}
		return separator;
	}

	public static Separator getSeparator(String sclass, String height, String width) {
		Separator separator = new Separator();
		if (StringUtils.isNotBlank(sclass))
			separator.setClass(sclass);
		if (StringUtils.isNotBlank(height))
			separator.setHeight(height);
		if (StringUtils.isNotBlank(width))
			separator.setWidth(width);
		return separator;
	}

	public static Combobox getCombobox(String placeholder, boolean readonly, String width) {
		/*Combobox combobox = new InfodiggCombobox();*/
		Combobox combobox = new Combobox();
		if (StringUtils.isNotBlank(placeholder)) {
			combobox.setPlaceholder(placeholder);
		}
		if (StringUtils.isNotBlank(width)) {
			combobox.setWidth(width);
		}
		combobox.setReadonly(readonly);
		return combobox;
	}

	public static Comboitem getComboitem(String label, String description, Object value) {
		Comboitem comboitem = new Comboitem();
		if (StringUtils.isNotBlank(label))
			comboitem.setLabel(label);
		if (StringUtils.isNotBlank(description))
			comboitem.setDescription(description);
		if (value != null)
			comboitem.setValue(value);
		return comboitem;
	}

	public static Comboitem getComboitem(String label, String description, String tooltiptext, String imageSrc,
										 org.zkoss.image.Image image, Object value) {
		Comboitem comboitem = getComboitem(label, description, value);
		if (StringUtils.isNotBlank(tooltiptext)) {
			comboitem.setTooltiptext(tooltiptext);
		}
		if (StringUtils.isNotBlank(imageSrc)) {
			comboitem.setImage(imageSrc);
		}
		if (image != null) {
			comboitem.setImageContent(image);
		}
		return comboitem;
	}

	public static void clearCombobox(Combobox combobox) {
		combobox.getItems().clear();
		unSelectCombobox(combobox);
	}

	public static void unSelectCombobox(Combobox combobox) {
		combobox.setValue("");
		combobox.setSelectedIndex(-1);
	}

	public static void setComboboxValue(Combobox combobox, Object value) {
		unSelectCombobox(combobox);
		if (value == null) {
			return;
		}
		for (Comboitem item : combobox.getItems()) {
			if (value.equals(item.getValue())) {
				combobox.setSelectedItem(item);
				return;
			}
		}
	}

	public static void addComboboxFilter(Combobox combobox) {
		combobox.addEventListener(Events.ON_CHANGING, (event) -> {
			String value = ((InputEvent) event).getValue().toLowerCase();
			for (Comboitem comboitem : combobox.getItems()) {
				comboitem.setVisible(value.isEmpty() || comboitem.getLabel().toLowerCase().contains(value));
			}
		});
	}

	public static Comboitem getComboboxItemByValue(Combobox combobox, Object value) {
		if (value == null)
			return null;
		for (Comboitem item : combobox.getItems()) {
			if (value.equals(item.getValue())) {
				return item;
			}
		}
		return null;
	}

	public static void setComboboxItemByValue(Combobox combobox, Object value) {
		Comboitem selected = getComboboxItemByValue(combobox, value);
		combobox.setSelectedItem(selected);
	}

	public static Menuitem getMenuitem(String label, String iconSclass) {
		return getMenuitem(label, iconSclass, null);
	}

	public static Menuitem getMenuitem(String label, String iconSclass, EventListener<? extends Event> onClick, boolean visible) {
		Menuitem menuItem = getMenuitem(label, iconSclass, onClick);
		menuItem.setVisible(visible);
		return menuItem;
	}

	public static Menuitem getMenuitem(String label, String iconSclass, EventListener<? extends Event> onClick) {
		Menuitem menuitem = new Menuitem();
		if (StringUtils.isNotBlank(label)) {
			menuitem.setLabel(label);
		}
		if (StringUtils.isNotBlank(iconSclass)) {
			menuitem.setIconSclass(iconSclass);
		}
		if (onClick != null) {
			menuitem.addEventListener(Events.ON_CLICK, onClick);
		}
		return menuitem;
	}

	public static Switch getSwitch(boolean checked) {
		return new Switch(checked);
	}

	public static Switch getSwitch(boolean checked, SerializableEventListener<Event> onToggleEvent) {
		Switch aux = new Switch(checked);
		aux.addEventListener(Switch.ON_TOGGLE, onToggleEvent);
		return aux;
	}

	public static Style getStyle(String src, String content) {
		Style style = new Style();
		if (StringUtils.isNotBlank(src))
			style.setSrc(src);
		if (StringUtils.isNotBlank(content))
			style.setContent(content);
		return style;
	}

	public static Tree getTree(boolean treecol, boolean treefoot, String tags) {
		Tree tree = new Tree();
		tree.appendChild(new Treechildren());
		if (StringUtils.isNotBlank(tags)) {
			tree.setNonselectableTags(tags);
		}
		if (treecol) {
			tree.appendChild(new Treecols());
		}
		if (treefoot) {
			tree.appendChild(new Treefoot());
		}
		return tree;
	}

	public static Treeitem getTreeitem(String label, String image, boolean parent, Object value) {
		Treeitem treeitem = new Treeitem();
		if (StringUtils.isNotBlank(image)) {
			treeitem.setImage(image);
		}
		if (StringUtils.isNotBlank(label)) {
			treeitem.setLabel(label);
		}
		if (parent) {
			treeitem.appendChild(new Treechildren());
		}
		treeitem.setValue(value);
		return treeitem;
	}

	public static Treeitem getTreeitem(String label, boolean parent, Object value) {
		return getTreeitem(label, "", parent, value);
	}

	public static Treecol getTreecol(String label, String align, String width) {
		Treecol column = new Treecol();
		if (StringUtils.isNotBlank(label)) {
			column.setLabel(label);
		}
		if (StringUtils.isNotBlank(align)) {
			column.setAlign(align);
		}
		if (StringUtils.isNotBlank(width)) {
			column.setWidth(width);
		}
		return column;
	}

	public static Treecell getTreecell(String label, Component child) {
		Treecell treecell = new Treecell();
		if (StringUtils.isNotBlank(label)) {
			treecell.setLabel(label);
		}
		if (child != null) {
			treecell.appendChild(child);
		}
		return treecell;
	}

	public static Tabbox getTabbox() {
		Tabbox tabbox = new Tabbox();
		tabbox.appendChild(new Tabs());
		tabbox.appendChild(new Tabpanels());
		return tabbox;
	}

	public static Tab getTab(String label) {
		Tab tab = new Tab();
		if (StringUtils.isNotBlank(label))
			tab.setLabel(label);
		return tab;
	}

	public static Tabpanel getTabpanel(Component child) {
		Tabpanel tabpanel = new Tabpanel();
		if (child != null)
			tabpanel.appendChild(child);
		return tabpanel;
	}

	public static Checkbox getCheckbox(String label, boolean checked, boolean disabled, Object value) {
		Checkbox checkbox = new Checkbox();
		if (StringUtils.isNotBlank(label)) {
			checkbox.setLabel(label);
		}
		checkbox.setChecked(checked);
		checkbox.setDisabled(disabled);
		checkbox.setValue(value);
		return checkbox;
	}

	public static Checkbox getCheckbox(String label, Object value) {
		return getCheckbox(label, false, false, value);
	}

	public static Checkbox getReadOnlySwitchCheckbox(boolean checked) {
		Checkbox checkbox = ComponentsUtil.getCheckbox(null, checked, true, null);
		checkbox.setMold("switch");
		return checkbox;
	}

	public static Radiogroup getRadiogroup(String id) {
		Radiogroup radiogroup = new Radiogroup();
		radiogroup.setId(id);
		return radiogroup;
	}

	public static Radio getRadio(String label, Object value) {
		Radio radio = new Radio();
		if (StringUtils.isNotBlank(label)) {
			radio.setLabel(label);
		}
		radio.setValue(value);
		return radio;
	}

	public static Radio getRadio(String label, Radiogroup radiogroup, Object value) {
		Radio radio = getRadio(label, value);
		if (radiogroup != null) {
			radio.setRadiogroup(radiogroup);
		}
		return radio;
	}

	public static Datebox getDatebox(Date value, boolean readonly, String placeholder, String format, String constraint) {
		return getDatebox(value, readonly, placeholder, format, constraint, null, 0);
	}

	public static Datebox getDatebox(Date value, boolean readonly, String placeholder, String format, String constraint, String selectLevel) {
		return getDatebox(value, readonly, placeholder, format, constraint, selectLevel, 0);
	}

	public static Datebox getDatebox(Date value, boolean readonly, String placeholder, String format, String constraint, String selectLevel, int hflex) {
		Datebox datebox = new Datebox(value);
		datebox.setReadonly(readonly);
		if (StringUtils.isNotBlank(placeholder)) {
			datebox.setPlaceholder(placeholder);
		}
		if (StringUtils.isNotBlank(format)) {
			datebox.setFormat(format);
		}
		if (StringUtils.isNotBlank(constraint)) {
			datebox.setConstraint(constraint);
		}
		if (StringUtils.isNotBlank(selectLevel)) {
			datebox.setSelectLevel(selectLevel);
		}
		if(hflex > 0) {
			datebox.setHflex(hflex+"");
		}else {
			datebox.setWidth("100%");
		}
		return datebox;
	}

	public static Rating getRating(int max, int value) {
		Rating rating = new Rating();
		if (max > 0) {
			rating.setMax(max);
		}
		if (value > 0) {
			rating.setRating(value);
		}
		return rating;
	}

	public static Popup getPopup(String sclass, String width, Component... components) {
		Popup popup = new Popup();
		if (StringUtils.isNotBlank(sclass)) {
			popup.setClass(sclass);
		}
		if (StringUtils.isNotBlank(width)) {
			popup.setWidth(width);
		}
		if (components != null) {
			for (Component component : components) {
				if (component != null) {
					popup.appendChild(component);
				}
			}
		}
		return popup;
	}

	public static Popup getPopup(String width, Component... components) {
		return getPopup("", width, components);
	}

	public static Popup getPopup(String id, String width, String sclass, Component... components) {
		Popup popup = getPopup(sclass, width, components);
		if (StringUtils.isNotBlank(id)) {
			popup.setId(id);
		}
		return popup;
	}

	public static Html getHTML(String content, String sclass) {
		Html html = new Html();
		if (StringUtils.isNotBlank(content))
			html.setContent(content);
		if (StringUtils.isNotBlank(sclass))
			html.setClass(sclass);
		return html;
	}

	public static Slider getSlider(String width, int minPos, int maxPos, int step, int curpos) {
		Slider slider = new Slider();
		if (StringUtils.isNotBlank(width)) {
			slider.setWidth(width);
		}
		slider.setMinpos(minPos);
		slider.setMaxpos(maxPos);
		slider.setStep(step);
		slider.setCurpos(curpos);
		return slider;
	}

	public static Slider getSlider(String width, double minPos, double maxPos, double step, double curpos) {
		Slider slider = new Slider();
		if (StringUtils.isNotBlank(width)) {
			slider.setWidth(width);
		}
		slider.setMinpos(minPos);
		slider.setMaxpos(maxPos);
		slider.setStep(step);
		slider.setMode("decimal");
		slider.setCurpos(curpos);
		return slider;
	}

	public static Doublespinner getDoublespinner(String width, double step, double value, boolean readonly) {
		Doublespinner doublespinner = new Doublespinner();
		if (StringUtils.isNotBlank(width)) {
			doublespinner.setWidth(width);
		}
		doublespinner.setStep(step);
		doublespinner.setValue(value);
		doublespinner.setReadonly(readonly);
		return doublespinner;
	}

	public static Hlayout getHlayoutRating(String spacing, double totalRating, String sclassEmpty, String sclassFill) {
		Hlayout hlayout_stars_rating = getHlayout(spacing, "middle");
		for (int i = 1; i < 6; i++) {
			String sclass = String.format("fa fa-star %s", sclassEmpty);
			if (i <= totalRating) {
				sclass = String.format("fa fa-star %s", sclassFill);
			} else {
				if ((i - totalRating) < 1) {
					sclass = String.format("fa fa-star-half-o %s", sclassFill);
				}
			}
			hlayout_stars_rating.appendChild(ComponentsUtil.getSpan(sclass, null));
		}
		return hlayout_stars_rating;
	}

	public static Bandbox getBandbox(boolean buttonVisible, boolean autodrop, Component... components) {
		Bandbox bandbox = new Bandbox();
		bandbox.setWidth("100%");
		bandbox.setButtonVisible(buttonVisible);
		bandbox.setAutodrop(autodrop);
		Bandpopup bandpopup = new Bandpopup();
		bandbox.appendChild(bandpopup);
		if (components != null) {
			for (Component component : components) {
				if (component != null) {
					bandpopup.appendChild(component);
				}
			}
		}
		return bandbox;
	}

	public static Toolbarbutton getToolBarButton(String label, String iconClass, String sClass,
												 EventListener<MouseEvent> clickEvent) {
		Toolbarbutton button = new Toolbarbutton();
		if (!StringUtils.isBlank(label)) {
			button.setLabel(label);
		}
		if (!StringUtils.isBlank(iconClass)) {
			button.setIconSclass(iconClass);
		}
		if (!StringUtils.isBlank(sClass)) {
			button.setSclass(sClass);
		}
		if (clickEvent != null) {
			button.addEventListener(Events.ON_CLICK, clickEvent);
		}
		return button;
	}

	public static Separator getSeparatorBar(boolean horizontal) {
		Separator separator = new Separator(horizontal ? "horizontal" : "vertical");
		separator.setBar(true);
		return separator;
	}

	public static Spinner getSpinner(int value, int step, String width, boolean readonly, Integer min, Integer max) {
		Spinner spinner = new Spinner(value);
		if (step > 0) {
			spinner.setStep(step);
		}
		if (StringUtils.isNotBlank(width)) {
			spinner.setWidth(width);
		}
		spinner.setReadonly(readonly);
		if (min != null && max != null) {
			spinner.setConstraint(String.format("min %s max %s", min, max));
		} else if (min != null) {
			spinner.setConstraint(String.format("min %s", min));
		} else if (max != null) {
			spinner.setConstraint(String.format("max %s", max));
		}
		return spinner;
	}

	public static void fillComboboxWithStringList(Combobox combobox, List<String> list) {
		combobox.getItems().clear();

		if (list!=null) {
			for(String elem : list) {
				combobox.appendChild(getComboitem(elem, null, elem));
			}
		}
	}

	public static Listcell getListcellWithTooltipText(String label, String tooltipText, Component child) {
		Listcell result = getListcell(label, child);
		result.setTooltiptext(tooltipText);
		return result;
	}

	public static Window getWindow(String title, String border, boolean closable, String width, boolean visible, Component... components) {
		Window window = new Window(title, border, closable);
		if(StringUtils.isNotBlank(width)) {
			window.setWidth(width);
		}
		window.setVisible(visible);
		if (components != null) {
			for (Component component : components) {
				if (component != null) {
					window.appendChild(component);
				}
			}
		}
		return window;
	}

	public static <T> T getComboboxSelectedItem(Combobox combobox) {
		Comboitem selectedItem = combobox.getSelectedItem();
		if (selectedItem == null) {
			return null;
		}
		return selectedItem.getValue();
	}

	public static Component createComponents(String uri, Component parent, Map<?, ?> arg) {
		//CommonsController.registerPageAccess(uri);
		if (arg == null) {
			arg = new HashMap<>();
		}
		Component result = null;
		try {
			result = Executions.createComponents(uri, parent, arg);
		} catch (Throwable e) {
			//IKernel.getBaseLogger().error("Error creating zul:" + uri,e);
			throw e;
		}
		return result;
	}
}