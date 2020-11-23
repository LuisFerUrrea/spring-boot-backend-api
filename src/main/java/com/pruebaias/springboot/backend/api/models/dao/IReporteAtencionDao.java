package com.pruebaias.springboot.backend.api.models.dao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pruebaias.springboot.backend.api.models.entity.ReporteAtencion;

public interface IReporteAtencionDao extends JpaRepository<ReporteAtencion,Long> {

	public List<ReporteAtencion> findByidTecnico(String idTecnico);
}
