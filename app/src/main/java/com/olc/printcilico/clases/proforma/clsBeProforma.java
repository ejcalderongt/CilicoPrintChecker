package com.olc.printcilico.clases.proforma;

import java.util.ArrayList;

public class clsBeProforma {
    public int idproforma = 0;
    public int IdUsuarioServicio = 0;
    public String fecha_recibo = "";
    public double num_proforma = 0;
    public String serie_proforma = "";
    public boolean anulado = false;
    public int idusuario = 0;
    public String fecha_creacion = "";
    public int idusuario_modifica = 0;
    public String fecha_modifica = "";
    public int mes_pago = 0;
    public int año_pago = 0;
    public boolean Impresion = false;
    public String proveedor = "";
    public String observacion = "";
    public int IdPeriodoParametros = 0;
    public boolean pagol = false;
    public String fecha_ultimo_pago = "";
    public int Con_hh = 0;
    public int IdTecnico = 0;
    public ArrayList<clsBeProforma_detalle> detalle = new ArrayList<>();

    public String StatCom = "N";
}
