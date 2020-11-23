package com.pruebaias.springboot.backend.api.models.services;
import java.util.List;
import com.pruebaias.springboot.backend.api.models.entity.ReporteAtencion;

public interface IReporteAtencionService {
	
	public List<ReporteAtencion> findAll();	   
	
	public ReporteAtencion findById(Long id);
	
	public ReporteAtencion save(ReporteAtencion reporteAtencion);
	
	public void delete(Long id);
	
	public List<ReporteAtencion> findByIdtecnico(String idtecnico);
}
