package com.italoweb.gestorfinan.model.compra;

import java.time.LocalDate;
import java.util.List;

import com.italoweb.gestorfinan.model.MedioPago;
import com.italoweb.gestorfinan.model.Proveedor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compras {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "proveedor_id", nullable = false)
  private Proveedor proveedor;

  @Column(name = "numero_factura", length = 25)
  private String numeroFactura;

  @Column(name = "fecha_ingreso", nullable = false)
  private LocalDate fechaIngreso;

  @Column(name = "fecha_pago", nullable = false)
  private LocalDate fechaPago;

  @ManyToOne(optional = false)
  @JoinColumn(name = "medio_pago", nullable = false)
  private MedioPago medioPago;

  @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CompraDetalle> detalles;

}
