package com.italoweb.gestorfinan.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movimiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "concepto_id", nullable = false)
    private Concepto concepto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medio_pago_id", nullable = false)
    private MedioPago medioPago;

    @Column(nullable = false, name = "tipo_movimiento")
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false, name = "valor")
    private BigDecimal valor;

    @Column(nullable = false, name = "fecha")
    private Date fecha;

    @Column(nullable = true, name = "descripcion")
    private String descripcion;

    
}

