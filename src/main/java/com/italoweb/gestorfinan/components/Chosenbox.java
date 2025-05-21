package com.italoweb.gestorfinan.components;

import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;

import java.util.*;
import java.util.stream.Collectors;

public class Chosenbox extends Div {

    private static final long serialVersionUID = -3045073631333918487L;
	private final Div inputWrapper = new Div();
    private final Textbox inputBox = new Textbox();
    private final Listbox resultList = new Listbox();
    private final Set<String> selectedItems = new LinkedHashSet<>();
    private List<String> allItems = new ArrayList<>();
    private boolean multiple = true;
    private String wrapMode = "wrap"; // valor por defecto

    public Chosenbox() {
        setSclass("form-group");

        buildUI();
        applyWrapMode();
        setupListeners();
    }

    private void buildUI() {
        // Contenedor que simula el textbox con chips
        inputWrapper.setSclass("d-flex flex-wrap align-items-center gap-1");
        inputWrapper.setStyle("min-height: 38px; max-height: 120px; overflow-y: auto; border: 1px solid #ced4da; border-radius: .375rem; ");
        //inputWrapper.setSpacing("5px");

        // Input real
        inputBox.setSclass("border-0 flex-grow-1");
        inputBox.setStyle("min-width: 120px; outline: none; box-shadow: none;");
        inputBox.setPlaceholder("");
        inputWrapper.appendChild(inputBox);

        // Resultados
        resultList.setVisible(false);
        resultList.setSclass("form-select mt-1");
        resultList.setHeight("120px");
        resultList.setHflex("1");

        // Agregamos al componente principal
        appendChild(inputWrapper);
        appendChild(resultList);
    }

    private void setupListeners() {
        inputBox.addEventListener(Events.ON_CHANGING, e -> {
            InputEvent input = (InputEvent) e;
            String query = input.getValue().toLowerCase();

            List<String> filtered = allItems.stream()
                    .filter(opt -> opt.toLowerCase().contains(query))
                    .filter(opt -> !selectedItems.contains(opt))
                    .collect(Collectors.toList());

            resultList.getItems().clear();

            for (String item : filtered) {
                Listitem li = new Listitem(item);
                li.setValue(item);
                li.addEventListener(Events.ON_CLICK, ev -> selectItem(item));
                resultList.appendChild(li);
            }

            resultList.setVisible(!filtered.isEmpty());
        });

        inputBox.addEventListener(Events.ON_CLICK, e -> {
            List<String> filtered = allItems.stream()
                    .filter(opt -> !selectedItems.contains(opt))
                    .collect(Collectors.toList());

            resultList.getItems().clear();

            for (String item : filtered) {
                Listitem li = new Listitem(item);
                li.setValue(item);
                li.addEventListener(Events.ON_CLICK, ev -> selectItem(item));
                resultList.appendChild(li);
            }

            resultList.setVisible(!filtered.isEmpty());
        });
    }

    private void selectItem(String item) {
        if (!multiple) {
            selectedItems.clear(); // Borra todos si es modo único
        }

        selectedItems.add(item);
        updateUI();
        inputBox.setValue("");
        resultList.setVisible(false);
    }

    private void updateUI() {
        // Eliminamos los chips antes del textbox (dejamos solo el textbox)
        inputWrapper.getChildren().clear();

        for (String item : selectedItems) {
            Hbox chip = new Hbox();
            chip.setSclass("badge bg-primary me-1 mb-1 px-2 d-inline-flex align-items-center");
            chip.setStyle("background: var(--color-primario);");
            Label label = new Label(item);
            label.setSclass("me-1");

            Toolbarbutton close = new Toolbarbutton("", "z-icon-times");
            close.setSclass("btn-close btn-close-white btn-sm");
            close.setTooltiptext("Eliminar");
            close.addEventListener(Events.ON_CLICK, e -> {
                selectedItems.remove(item);
                updateUI();
            });

            chip.appendChild(label);
            chip.appendChild(close);
            inputWrapper.appendChild(chip);
        }

        // Volvemos a insertar el input al final
        inputWrapper.appendChild(inputBox);
    }

    private void applyWrapMode() {
        if (wrapMode.equals("wrap")) {
            inputWrapper.setSclass("form-control d-flex flex-wrap align-items-start gap-1 px-2 py-1");
            inputWrapper.setStyle("min-height: 38px; max-height: 120px; overflow-y: auto; overflow-x: hidden; border: 1px solid #ced4da; border-radius: .375rem; white-space: normal;");
        } else if (wrapMode.equals("scroll")) {
            inputWrapper.setSclass("form-control d-flex align-items-start gap-1 px-2 py-1");
            inputWrapper.setStyle("min-height: 38px; white-space: nowrap; overflow-x: auto; overflow-y: hidden; border: 1px solid #ced4da; border-radius: .375rem;");
        }
    }

    public void setModel(List<String> items) {
        this.allItems = new ArrayList<>(items);
    }

    public Set<String> getSelectedItems() {
        return selectedItems;
    }

    public void clearSelection() {
        selectedItems.clear();
        updateUI();
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setWrapMode(String mode) {
        if (!mode.equals("wrap") && !mode.equals("scroll")) {
            throw new IllegalArgumentException("wrapMode debe ser 'wrap' o 'scroll'");
        }
        this.wrapMode = mode;
        applyWrapMode(); // aplica inmediatamente si ya fue creado
    }

    public String getWrapMode() {
        return wrapMode;
    }
}