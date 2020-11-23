package com.pruebaias.springboot.backend.api.models.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pruebaias.springboot.backend.api.models.dao.IReporteAtencionDao;
import com.pruebaias.springboot.backend.api.models.entity.ReporteAtencion;

@Service
public class ReporteAtencionServiceImpl implements IReporteAtencionService {

	@Autowired
	private IReporteAtencionDao reporteAtencionDao;
	
	@Override
	@Transactional(readOnly=true)
	public List<ReporteAtencion> findAll() {
		return (List<ReporteAtencion>)reporteAtencionDao.findAll();
	}	

	@Override
	@Transactional(readOnly=true)
	public ReporteAtencion findById(Long id) {
		return reporteAtencionDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public ReporteAtencion save(ReporteAtencion reporteAtencion) {		
		return reporteAtencionDao.save(reporteAtencion);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		reporteAtencionDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public List<ReporteAtencion> findByIdtecnico(String idtecnico) {
		return reporteAtencionDao.findByidTecnico(idtecnico);
	}
	

}
