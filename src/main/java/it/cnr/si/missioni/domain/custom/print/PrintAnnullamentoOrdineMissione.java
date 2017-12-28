package it.cnr.si.missioni.domain.custom.print;


public class PrintAnnullamentoOrdineMissione extends PrintMissione{

    private String motivoAnnullamento;

	public String getMotivoAnnullamento() {
		return motivoAnnullamento;
	}

	public void setMotivoAnnullamento(String motivoAnnullamento) {
		this.motivoAnnullamento = motivoAnnullamento;
	}

}
