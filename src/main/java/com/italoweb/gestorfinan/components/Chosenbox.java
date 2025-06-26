package com.italoweb.gestorfinan.components;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Chosenbox<T> extends Div {

    private static final long serialVersionUID = -3045073631333918487L;

    private final Div inputWrapper = new Div();
    private final Textbox inputBox = new Textbox();
    private final Listbox resultList = new Listbox();

    private final Set<T> selectedItems = new LinkedHashSet<>();
    private final Map<String, T> itemMap = new LinkedHashMap<>();
    private List<String> allItems = new ArrayList<>();

    private boolean multiple = true;
    private String wrapMode = "wrap"; // por defecto
    private Function<T, String> displayFunction = Object::toString;

    public Chosenbox() {
        setSclass("form-group");
        buildUI();
        applyWrapMode();
        setupListeners();
    }

    private void buildUI() {
        inputWrapper.setSclass("d-flex flex-wrap align-items-center gap-1");
        inputWrapper.setStyle("min-height: 38px; max-height: 120px; overflow-y: auto; border: 1px solid #ced4da; border-radius: .375rem;");

        inputBox.setSclass("border-0 flex-grow-1");
        inputBox.setStyle("min-width: 120px; outline: none; box-shadow: none;");
        inputBox.setPlaceholder("");
        inputWrapper.appendChild(inputBox);

        resultList.setVisible(false);
        resultList.setSclass("form-select mt-1");
        resultList.setHeight("120px");
        resultList.setHflex("1");

        appendChild(inputWrapper);
        appendChild(resultList);
    }

    private void setupListeners() {
        inputBox.addEventListener(Events.ON_CHANGING, e -> {
            InputEvent input = (InputEvent) e;
            String query = input.getValue().toLowerCase();

            List<String> filtered = allItems.stream()
                    .filter(opt -> opt.toLowerCase().contains(query))
                    .filter(opt -> !isSelected(opt))
                    .collect(Collectors.toList());

            resultList.getItems().clear();

            for (String itemLabel : filtered) {
                Listitem li = new Listitem(itemLabel);
                li.setValue(itemLabel);
                li.addEventListener(Events.ON_CLICK, ev -> selectItem(itemLabel));
                resultList.appendChild(li);
            }

            resultList.setVisible(!filtered.isEmpty());
        });

        inputBox.addEventListener(Events.ON_CLICK, e -> {
            List<String> filtered = allItems.stream()
                    .filter(opt -> !isSelected(opt))
                    .collect(Collectors.toList());

            resultList.getItems().clear();

            for (String itemLabel : filtered) {
                Listitem li = new Listitem(itemLabel);
                li.setValue(itemLabel);
                li.addEventListener(Events.ON_CLICK, ev -> selectItem(itemLabel));
                resultList.appendChild(li);
            }

            resultList.setVisible(!filtered.isEmpty());
        });
    }

    private boolean isSelected(String label) {
        T obj = itemMap.get(label);
        return obj != null && selectedItems.contains(obj);
    }

    private void selectItem(String label) {
        T item = itemMap.get(label);
        if (item == null) return;

        if (!multiple) {
            selectedItems.clear();
        }

        selectedItems.add(item);
        updateUI();
        inputBox.setValue("");
        resultList.setVisible(false);
    }

    private void updateUI() {
        inputWrapper.getChildren().clear();

        for (T item : selectedItems) {
            String label = displayFunction.apply(item);

            Hbox chip = new Hbox();
            chip.setSclass("badge bg-primary me-1 mb-1 px-2 d-inline-flex align-items-center");
            chip.setStyle("background: var(--color-primario);");
            Label lbl = new Label(label);
            lbl.setSclass("me-1");

            Toolbarbutton close = new Toolbarbutton("", "z-icon-times");
            close.setSclass("btn-close btn-close-white btn-sm");
            close.setTooltiptext("Eliminar");
            close.addEventListener(Events.ON_CLICK, e -> {
                selectedItems.remove(item);
                updateUI();
            });

            chip.appendChild(lbl);
            chip.appendChild(close);
            inputWrapper.appendChild(chip);
        }

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

    // ======================= MÉTODOS PÚBLICOS =======================

    public void setModel(List<T> items, Function<T, String> displayFunction) {
        if (items == null || displayFunction == null) return;

        this.displayFunction = displayFunction;
        this.itemMap.clear();
        this.allItems.clear();

        for (T item : items) {
            String label = displayFunction.apply(item);
            if (itemMap.containsKey(label)) {
                throw new IllegalArgumentException("Label duplicado: '" + label + "'. Asegúrate de que los labels sean únicos.");
            }
            itemMap.put(label, item);
            allItems.add(label);
        }
    }

    public void setSelectedItems(Collection<T> items) {
        if (items == null) return;

        if (!multiple && items.size() > 1) {
            throw new IllegalArgumentException("Este componente está en modo selección única y no puede aceptar múltiples valores.");
        }

        selectedItems.clear();
        selectedItems.addAll(items);
        updateUI();
    }

    public Set<T> getSelectedItems() {
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
        applyWrapMode();
    }

    public String getWrapMode() {
        return wrapMode;
    }
}
