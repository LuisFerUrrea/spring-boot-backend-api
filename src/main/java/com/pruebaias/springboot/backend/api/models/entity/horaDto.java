package com.pruebaias.springboot.backend.api.models.entity;

public class horaDto {
	public int hBasica,hNocturna,hDominical,hBasicaExtra,hNocturnaExtra,hDominicalExtra,topeHoraSemanal=0;
	boolean superoTopeHoras=false;
	public int gethBasica() {
		return hBasica;
	}
	public void sethBasica(int hBasica) {
		this.hBasica = hBasica;
	}
	public int gethNocturna() {
		return hNocturna;
	}
	public void sethNocturna(int hNocturna) {
		this.hNocturna = hNocturna;
	}
	public int gethDominical() {
		return hDominical;
	}
	public void sethDominical(int hDominical) {
		this.hDominical = hDominical;
	}
	public int gethBasicaExtra() {
		return hBasicaExtra;
	}
	public void sethBasicaExtra(int hBasicaExtra) {
		this.hBasicaExtra = hBasicaExtra;
	}
	public int gethNocturnaExtra() {
		return hNocturnaExtra;
	}
	public void sethNocturnaExtra(int hNocturnaExtra) {
		this.hNocturnaExtra = hNocturnaExtra;
	}
	public int gethDominicalExtra() {
		return hDominicalExtra;
	}
	public void sethDominicalExtra(int hDominicalExtra) {
		this.hDominicalExtra = hDominicalExtra;
	}
	public int getTopeHoraSemanal() {
		return topeHoraSemanal;
	}
	public void setTopeHoraSemanal(int topeHoraSemanal) {
		this.topeHoraSemanal = topeHoraSemanal;
	}
	public boolean isSuperoTopeHoras() {
		return superoTopeHoras;
	}
	public void setSuperoTopeHoras(boolean superoTopeHoras) {
		this.superoTopeHoras = superoTopeHoras;
	}
	
}
