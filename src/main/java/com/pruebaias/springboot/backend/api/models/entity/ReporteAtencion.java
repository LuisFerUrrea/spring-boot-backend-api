package com.pruebaias.springboot.backend.api.models.entity;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name="reporte_atenciones")
public class ReporteAtencion implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;	
	
	@NotEmpty(message ="no puede estar vacio")	
	@Size(min=4,max=20,message="el tamaño tiene que estar entre 4 y 20 caracteres")
	@Column(name="id_tecnico", nullable=false)	
	private String idTecnico;
	
	@NotEmpty(message ="no puede estar vacio")
	@Size(min=4,max=20,message="el tamaño tiene que estar entre 4 y 20 caracteres")
	@Column(name="id_servicio",nullable=false)		
	private String idServicio;
	
	@NotEmpty(message ="no puede estar vacio")
	@Column(name="fecha_inicio",nullable=false)	
	private Timestamp fechaInicio;		
	
	@NotEmpty(message ="no puede estar vacio")
	@Column(name="fecha_fin",nullable=false)	
	private Timestamp fechaFin;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdTecnico() {
		return idTecnico;
	}

	public void setIdTecnico(String idTecnico) {
		this.idTecnico = idTecnico;
	}

	public String getIdServicio() {
		return idServicio;
	}

	public void setIdServicio(String idServicio) {
		this.idServicio = idServicio;
	}	

	

	public Timestamp getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Timestamp fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Timestamp getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Timestamp fechaFin) {
		this.fechaFin = fechaFin;
	}

	/*public int getAnio() {
		return fechaInicio.getYear();
	}
	
	public int getSemana() {		
		return fechaInicio.get;
	}
	
	public int getDayWeek() {			  
		return fechaInicio.get(Calendar.DAY_OF_WEEK);
	}*/



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
