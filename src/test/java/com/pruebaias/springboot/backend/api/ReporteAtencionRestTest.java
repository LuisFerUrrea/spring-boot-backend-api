package com.pruebaias.springboot.backend.api;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Timestamp;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.pruebaias.springboot.backend.api.controllers.ReporteAtencionRestController;
import com.pruebaias.springboot.backend.api.models.entity.ReporteAtencion;


class ReporteAtencionRestTest {	
	
		public static Logger logger=Logger.getLogger("global");		
		ReporteAtencionRestController objReporteAtencionRestController;
		ReporteAtencion reporteAtencion;;
	
		@BeforeEach
		public void before() {			
			objReporteAtencionRestController=new ReporteAtencionRestController();
			reporteAtencion=new ReporteAtencion();	
		
		}	
	
		@Test
		void testCreate(){	
			reporteAtencion.setIdTecnico("1017132");
			reporteAtencion.setIdServicio("1245687");	
		    Timestamp timestamp1 = Timestamp.valueOf("2020-09-13 03:52:30");
		    Timestamp timestamp2 = Timestamp.valueOf("2020-09-13 05:52:30");	  
		    reporteAtencion.setFechaInicio(timestamp1);
		    reporteAtencion.setFechaFin(timestamp2);   	    
		    ResponseEntity<?> r = objReporteAtencionRestController.create(reporteAtencion,null);
		    assertEquals(201,r.getStatusCodeValue());
		}
	
		@Test
		void testConsultar(){	
			 ResponseEntity<?> r = objReporteAtencionRestController.listReportes("1017132","2020-W02");
			 assertEquals(200,r.getStatusCodeValue());			 
		}
		
}
