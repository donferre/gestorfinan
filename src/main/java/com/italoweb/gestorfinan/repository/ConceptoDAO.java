package com.italoweb.gestorfinan.repository;

import com.italoweb.gestorfinan.model.Concepto;

public class ConceptoDAO extends GenericDAOImpl<Concepto, Long> {
    public ConceptoDAO() {
        super(Concepto.class);
    }
}
