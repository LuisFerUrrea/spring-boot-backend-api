package com.pruebaias.springboot.backend.api.controllers;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pruebaias.springboot.backend.api.models.entity.horaDto;
import com.pruebaias.springboot.backend.api.models.entity.ReporteAtencion;
import com.pruebaias.springboot.backend.api.models.services.IReporteAtencionService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ReporteAtencionRestController {
	public static Logger logger=Logger.getLogger("global");	 
	horaDto pd=new horaDto();
	int horaTrabajadaNocturna,horaTrabajadaNocturna2,horaTrabajadaBasica,horaTrabajadaDominical=0;	
		
	@Autowired
	private IReporteAtencionService reporteAtencionService;
	
	@GetMapping("/reportes")
	public List<ReporteAtencion> index(){
		return reporteAtencionService.findAll();
	}
	
	@PostMapping("/reportes")
	public ResponseEntity<?> create(@Valid @RequestBody ReporteAtencion reporteAtencion,BindingResult result) {		
		ReporteAtencion reporteAtencionNew=null;
		Map<String,Object> response=new HashMap<>();
		if(result.hasErrors()) {			
			List<String> errors=result.getFieldErrors()
					.stream()
					.map(err->{
						return "El campo '"+err.getField()+"' "+err.getDefaultMessage();
					})
					.collect(Collectors.toList());					
					response.put("mensaje","Error al realizar la insercción en la base de datos");
					response.put("errors",errors);					
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.BAD_REQUEST);
		}	
		
		List<String> listError=new ArrayList<>();
		listError=this.validaciones(reporteAtencion);	
		if(listError!=null) {
			response.put("mensaje",listError);
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		try {
			reporteAtencionNew = reporteAtencionService.save(reporteAtencion);			
		}
		catch(DataAccessException e) {
			response.put("mensaje","Error al realizar la insercción en la base de datos");
			response.put("mensaje", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);			
		} 
		
		response.put("mensaje", "El reporte de la atención ha sido creado con exito!!");
		response.put("reporteAtencion", reporteAtencionNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
		//return new ResponseEntity<ReporteAtencion>(reporteAtencionNew,HttpStatus.CREATED);
	}
	
	@GetMapping("/reportes/{idtecnico}/{numberweek}")
	public ResponseEntity<?> listReportes(@PathVariable String idtecnico,@PathVariable String numberweek){	   
		resetVariables();
		Map<String,Object> response=new HashMap<>();
		List<ReporteAtencion> lsReporteAtencion=new ArrayList<>();				
		  try {
				fIllList(lsReporteAtencion,idtecnico,numberweek);	  
			    lsReporteAtencion.forEach(r->{	
			    	if(r.getFechaInicio().toLocalDateTime().getDayOfWeek().getValue()==1) {
			    		evaluacionDominical(r.getFechaInicio().toLocalDateTime(),r.getFechaFin().toLocalDateTime());
			    	}
			    	else {
			    		if(0 <= r.getFechaInicio().toLocalDateTime().getHour() && r.getFechaInicio().toLocalDateTime().getHour()<=7) {	 
				    		evaluacionPrimerTramo(r.getFechaInicio().toLocalDateTime(),r.getFechaFin().toLocalDateTime());
				    	}
				    	else if(7 <= r.getFechaInicio().toLocalDateTime().getHour() && r.getFechaInicio().toLocalDateTime().getHour()<=20) {	    		
				    		evaluacionSegundoTramo(r.getFechaInicio().toLocalDateTime(),r.getFechaFin().toLocalDateTime());
				    	}
				    	else {
				    		evaluacionTercerTramo(r.getFechaInicio().toLocalDateTime(),r.getFechaFin().toLocalDateTime());	
				    	}
			    	}	    	
			    	
			    });	  			  
			  response.put("reporteSemanal", pd);
			  return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			  
		  }	catch(Exception e) {
			  response.put("mensaje","Error al consultar el reporte semanal en la base de datos");
				response.put("mensaje", e.getMessage().concat(" : ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response,HttpStatus.BAD_REQUEST);
			}
	}
	
	private List<ReporteAtencion> fIllList(List<ReporteAtencion> lsReporteAtencion,String idtecnico,String numberWeek) {		
		 String[] anioWeek = numberWeek.split("-W0").length>1 ? numberWeek.split("-W0") : numberWeek.split("-W");
		 int anio=Integer.parseInt(anioWeek[0]);
		 int week=Integer.parseInt(anioWeek[1]);
		reporteAtencionService.findByIdtecnico(idtecnico).forEach(ra->{
		       ra.setFechaInicio(Timestamp.valueOf(ra.getFechaInicio().toLocalDateTime().plusHours(5)));
		       ra.setFechaFin(Timestamp.valueOf(ra.getFechaFin().toLocalDateTime().plusHours(5)));
		       WeekFields weekFields = WeekFields.of(Locale.getDefault());
		      
		    int weekNumber = ra.getFechaInicio().toLocalDateTime().get(weekFields.weekOfWeekBasedYear());
		    int year=ra.getFechaInicio().toLocalDateTime().getYear();     		    
		    if(weekNumber==week && year==anio) {
		    	lsReporteAtencion.add(ra);
		    }
		  }); 
		return lsReporteAtencion;
	}
	
	private void evaluacionDominical(LocalDateTime fechaInicio,LocalDateTime fechaFin) {
		horaTrabajadaDominical=fechaFin.getHour()*60 + fechaFin.getMinute() - (fechaInicio.getHour()*60+fechaInicio.getMinute());
		
		if(pd.isSuperoTopeHoras()) {
			pd.sethDominicalExtra(pd.gethDominicalExtra()+horaTrabajadaDominical);
		}
		else if((pd.getTopeHoraSemanal()+horaTrabajadaDominical)>2880) {
			pd.setSuperoTopeHoras(true);
			pd.sethDominical(pd.gethDominical()+2880-pd.getTopeHoraSemanal());
			pd.sethDominicalExtra(horaTrabajadaDominical-(2880-pd.getTopeHoraSemanal()));
			pd.setTopeHoraSemanal(2880);
		}
		else {
			pd.sethDominical(pd.gethDominical()+horaTrabajadaDominical);
			pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaDominical);
		}
	}
	
	private void evaluacionPrimerTramo(LocalDateTime fechaInicio,LocalDateTime fechaFin) {		
		horaTrabajadaNocturna = 7*60-(fechaInicio.getHour()*60+fechaInicio.getMinute());		
		if(fechaFin.getHour() < 7) {
			horaTrabajadaNocturna=fechaFin.getHour()*60+fechaFin.getMinute()-(fechaInicio.getHour()*60+fechaInicio.getMinute());
    		if(pd.isSuperoTopeHoras()) {	    			
    			pd.sethNocturnaExtra(pd.gethNocturnaExtra()+horaTrabajadaNocturna);		    		
    		}
    		else if((pd.getTopeHoraSemanal()+horaTrabajadaNocturna)>2880) {
    			
    			pd.setSuperoTopeHoras(true);
    			pd.sethNocturna(pd.gethNocturna()+2880-pd.getTopeHoraSemanal());
    			pd.sethNocturnaExtra(horaTrabajadaNocturna-(2880-pd.getTopeHoraSemanal()));
    			pd.setTopeHoraSemanal(2880);
    		}
    		else {
    			pd.sethNocturna(pd.gethNocturna()+horaTrabajadaNocturna);
    			pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaNocturna);
    		}
		}
		else if(7 <= fechaFin.getHour() && fechaFin.getHour()<20) {
			horaTrabajadaBasica= fechaFin.getHour()*60+fechaFin.getMinute()-7*60;			
			if(pd.isSuperoTopeHoras()) {
				pd.sethNocturnaExtra(pd.gethNocturnaExtra()+horaTrabajadaNocturna);
				pd.sethBasicaExtra(pd.gethBasicaExtra()+horaTrabajadaBasica);
			}
			else if(pd.getTopeHoraSemanal()+horaTrabajadaNocturna > 2880 || pd.getTopeHoraSemanal()+horaTrabajadaNocturna+horaTrabajadaBasica>2880) {
				pd.setSuperoTopeHoras(true);
				if(pd.getTopeHoraSemanal()+horaTrabajadaNocturna > 2880) {	    					
					pd.sethNocturna(pd.gethNocturna()+2880-pd.getTopeHoraSemanal());
					pd.sethNocturnaExtra(horaTrabajadaNocturna-(2880-pd.getTopeHoraSemanal()));
					pd.sethBasicaExtra(horaTrabajadaBasica);	    					
				}
				else {
					pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaNocturna);
					pd.sethNocturna(pd.gethNocturna()+horaTrabajadaNocturna);
					pd.sethBasica(pd.gethBasica()+2880-pd.getTopeHoraSemanal());
					pd.sethBasicaExtra(horaTrabajadaBasica-(2880-pd.getTopeHoraSemanal()));	    				
				}
				pd.setTopeHoraSemanal(2880);
			}
			else {
				pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaNocturna+horaTrabajadaBasica);
				pd.sethNocturna(pd.gethNocturna()+horaTrabajadaNocturna);
				pd.sethBasica(pd.gethBasica()+horaTrabajadaBasica);
			}
		}
		else {
			horaTrabajadaNocturna =7*60- (fechaInicio.getHour()+fechaInicio.getMinute());
			horaTrabajadaNocturna2=fechaFin.getHour()*60 + fechaFin.getMinute()-20*60;
			horaTrabajadaBasica= 20*60 - 7*60;
			if(pd.isSuperoTopeHoras()) {
				pd.sethNocturnaExtra(pd.gethNocturnaExtra()+horaTrabajadaNocturna + horaTrabajadaNocturna2);
				pd.sethBasicaExtra(pd.gethBasicaExtra()+horaTrabajadaBasica);
			}
			else if(pd.getTopeHoraSemanal()+horaTrabajadaNocturna > 2880 ||
					pd.getTopeHoraSemanal()+horaTrabajadaNocturna + horaTrabajadaBasica > 2880 ||
					pd.getTopeHoraSemanal()+horaTrabajadaNocturna + horaTrabajadaNocturna2 + horaTrabajadaBasica>2880) {
				pd.setSuperoTopeHoras(true);
				if(pd.getTopeHoraSemanal()+horaTrabajadaNocturna > 2880) {
					pd.sethNocturna(pd.gethNocturna()+2880-pd.getTopeHoraSemanal());
					pd.sethNocturnaExtra(horaTrabajadaNocturna-(2880-pd.getTopeHoraSemanal())+horaTrabajadaNocturna2);
					pd.sethBasicaExtra(horaTrabajadaBasica);
				}
				else if(pd.getTopeHoraSemanal()+horaTrabajadaNocturna + horaTrabajadaBasica > 2880){
					pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaNocturna);
					pd.sethNocturna(pd.gethNocturna()+horaTrabajadaNocturna);
					pd.sethBasica(pd.gethBasica()+2880-pd.getTopeHoraSemanal());
					pd.sethBasicaExtra(horaTrabajadaBasica-(2880-pd.getTopeHoraSemanal()));
				}
				else {
					pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaNocturna+horaTrabajadaBasica);
					pd.sethNocturna(pd.gethNocturna()+horaTrabajadaNocturna);
					pd.sethBasica(pd.gethBasica() + horaTrabajadaBasica);	    					
					pd.sethNocturna(pd.gethNocturna()+2880-pd.getTopeHoraSemanal());
					pd.sethNocturnaExtra(horaTrabajadaNocturna2-(2880-pd.getTopeHoraSemanal()));
				}
				pd.setTopeHoraSemanal(2880);
			}
			else {
				pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaNocturna+horaTrabajadaBasica+horaTrabajadaNocturna2);
				pd.sethNocturna(pd.gethNocturna()+horaTrabajadaNocturna+horaTrabajadaNocturna2);
				pd.sethBasica(pd.gethBasica()+horaTrabajadaBasica);
			}
		}
	}
	
	private void evaluacionSegundoTramo(LocalDateTime fechaInicio,LocalDateTime fechaFin) {
		if(7<=fechaFin.getHour() && fechaFin.getHour()<20) {    			
  		  horaTrabajadaBasica= fechaFin.getHour()*60 + fechaFin.getMinute() - (fechaInicio.getHour()*60+fechaInicio.getMinute());  
  		  
	    		if(pd.isSuperoTopeHoras()) {		    			
  				pd.sethBasicaExtra(pd.gethBasicaExtra()+horaTrabajadaBasica);
	    		}
	    		else if(pd.getTopeHoraSemanal() + horaTrabajadaBasica > 2880) {
	    			pd.setSuperoTopeHoras(true);
	    			pd.sethBasica(pd.gethBasica()+2880-pd.getTopeHoraSemanal());
	    			pd.sethBasicaExtra(horaTrabajadaBasica-(2880-pd.getTopeHoraSemanal()));
	    			pd.setTopeHoraSemanal(2880);
	    		}
	    		else {
	    			pd.sethBasica(pd.gethBasica()+horaTrabajadaBasica);
	    			pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaBasica);
	    		}
  		}
  		else {	    			
  			horaTrabajadaNocturna2=fechaFin.getHour()*60+ fechaFin.getMinute()-20*60;
  			horaTrabajadaBasica= 20*60 - (fechaInicio.getHour()*60+fechaInicio.getMinute());
  			if(pd.isSuperoTopeHoras()) {
  				pd.sethBasicaExtra(pd.gethBasicaExtra()+horaTrabajadaBasica);
  				pd.sethNocturnaExtra(pd.gethNocturnaExtra()+horaTrabajadaNocturna2);
  			}
  			else if(pd.getTopeHoraSemanal() + horaTrabajadaBasica >2880 || pd.getTopeHoraSemanal() + horaTrabajadaBasica+horaTrabajadaNocturna2 >2880) {
  				pd.setSuperoTopeHoras(true);
  				if(pd.getTopeHoraSemanal()+horaTrabajadaBasica > 2880) {	    					
  					pd.sethBasica(pd.gethBasica()+2880-pd.getTopeHoraSemanal());
  					pd.sethBasicaExtra(horaTrabajadaBasica-(2880-pd.getTopeHoraSemanal()));
  					pd.sethNocturnaExtra(horaTrabajadaNocturna2);	    					
  				}
  				else {
  					pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaBasica);
  					pd.sethBasica(pd.gethBasica()+horaTrabajadaBasica);
  					pd.sethNocturna(pd.gethNocturna()+2880-pd.getTopeHoraSemanal());
  					pd.sethNocturnaExtra(horaTrabajadaNocturna2-(2880-pd.getTopeHoraSemanal()));	    				
  				}
  				pd.setTopeHoraSemanal(2880);
  			}
  			else {
  				pd.sethBasica(pd.gethBasica() + horaTrabajadaBasica);
  				pd.sethNocturna(pd.gethNocturna() + horaTrabajadaNocturna2);
  				pd.setTopeHoraSemanal(pd.getTopeHoraSemanal() + horaTrabajadaBasica + horaTrabajadaNocturna2);
  			}
  		}
	}
	
	private void evaluacionTercerTramo(LocalDateTime fechaInicio,LocalDateTime fechaFin) {
		horaTrabajadaNocturna2 = fechaFin.getHour()*60+fechaFin.getMinute()-fechaInicio.getHour()*60+fechaInicio.getMinute();
		if(pd.isSuperoTopeHoras()) {	    			
			pd.sethNocturnaExtra(pd.gethNocturnaExtra()+horaTrabajadaNocturna2);		    		
		}
		else if((pd.getTopeHoraSemanal()+horaTrabajadaNocturna2)>2880) {
			pd.setSuperoTopeHoras(true);
			pd.sethNocturna(pd.gethNocturna()+2880-pd.getTopeHoraSemanal());
			pd.sethNocturnaExtra(horaTrabajadaNocturna2-(2880-pd.getTopeHoraSemanal()));
			pd.setTopeHoraSemanal(2880);
		}
		else {
			pd.sethNocturna(pd.gethNocturna()+horaTrabajadaNocturna2);
			pd.setTopeHoraSemanal(pd.getTopeHoraSemanal()+horaTrabajadaNocturna2);
		}   	
	}
	
    private List<String> validaciones(ReporteAtencion reporteAtencion) {
		boolean validation=false;
		List<String> listErrors=new ArrayList<>();
		if(reporteAtencion.getIdServicio()== null) {
			validation=true;
			listErrors.add("El campo id servicio es requerido.");			
		}
		if(reporteAtencion.getIdTecnico()==null) {
			validation=true;
			listErrors.add("El campo id tecnico es requerido.");			
		}
		if(reporteAtencion.getFechaInicio()==null) {
			validation=true;
			listErrors.add("El campo fecha inicio es requerido.");			
		}
		if(reporteAtencion.getFechaFin()==null) {
			validation=true;
			listErrors.add("El campo fecha fin es requerido.");			
		}
		if(reporteAtencion.getFechaFin().toLocalDateTime().isBefore(reporteAtencion.getFechaInicio().toLocalDateTime())) {
			validation=true;			
			listErrors.add("La fecha fin debe ser mayor que la fecha inicio.");			
		}
		
		LocalDateTime lt = LocalDateTime.now(); 		
		if(lt.isBefore(reporteAtencion.getFechaFin().toLocalDateTime().plusHours(5))) {
			validation=true;			
			listErrors.add("No pueden colocarse fechas superiores a la actual.");			
		}
		
		LocalDate dateIni = reporteAtencion.getFechaInicio().toLocalDateTime().plusHours(5).toLocalDate();
		LocalDate dateFin = reporteAtencion.getFechaFin().toLocalDateTime().plusHours(5).toLocalDate();			
		if(dateIni.compareTo(dateFin)!=0) {
			validation=true;
			listErrors.add("En las fechas el dia debe coincidir. Si requiere un nuevo día debe ingresar varios registros.");			
		}	
		
		if(validation) {	
			return listErrors;			
		}
		return null;
	}

    private void resetVariables() {
    	pd.sethBasica(0);
    	pd.sethBasicaExtra(0);
    	pd.sethDominical(0);
    	pd.sethDominicalExtra(0);
    	pd.sethNocturna(0);
    	pd.sethNocturnaExtra(0);
    	pd.setTopeHoraSemanal(0);
    	pd.setSuperoTopeHoras(false);
    	horaTrabajadaNocturna=0;
    	horaTrabajadaNocturna2=0;
    	horaTrabajadaBasica=0;
    	horaTrabajadaDominical=0;
    }
}
