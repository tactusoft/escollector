/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.tactusoft.escollector;

import co.tactusoft.escollector.model.FormComprobacion;
import co.tactusoft.escollector.model.FormComprobacionHorizonte;
import co.tactusoft.escollector.model.FormComprobacionHorizonteOpt;
import co.tactusoft.escollector.model.FormNotaCampo;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;

/**
 *
 * @author CSARMIENTO
 */
public class Principal {

    private static final String DIRECTORY_NAME = "C:\\escollector";
    private static final Logger LOGGER = Logger.getLogger(Principal.class.getName());

    private List<FormComprobacion> formComprobacionlist;
    private List<FormComprobacionHorizonte> formComprobacionHorizonteList;
    private List<FormComprobacionHorizonteOpt> formComprobacionHorizonteMotList;
    private List<FormComprobacionHorizonteOpt> formComprobacionHorizonteOptList;
    private List<FormNotaCampo> formNotaCampoList;

    public void loadFiles() {
        try {
            FileHandler fileHandler = new FileHandler("C:/escollector/escollector-log.log");
            LOGGER.addHandler(fileHandler);
            CustomRecordFormatter formatter = new CustomRecordFormatter();
            fileHandler.setFormatter(formatter);

            Files.walk(Paths.get(DIRECTORY_NAME)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.sqlite");
                    if (matcher.matches(filePath)) {
                        LOGGER.log(Level.INFO, "ESCOLLECTOR: Leyendo base de datos {0}", filePath.toString());
                        readDatabase(filePath.toString());
                        writeDatabase(filePath.toString());
                    }
                }
            });
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void readDatabase(String database) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + database)) {
                connection.setAutoCommit(false);
                LOGGER.log(Level.INFO, "ESCOLLECTOR: Abriendo base de datos {0}", database);

                LOGGER.log(Level.INFO, "ESCOLLECTOR: Leyendo tabla {0}", "form_comprobacion");
                formComprobacionlist = new LinkedList<>();
                try (Statement stmt = connection.createStatement(); ResultSet cur = stmt.executeQuery("SELECT id, nro_observacion, reconocedor, fecha_hora, longitud, latitud, altitud,\n"
                        + "nombre_sitio, epoca_climatica, dias_lluvia, pendiente_longitud, grado_erosion, tipo_movimiento, anegamiento,\n"
                        + "frecuencia, duracion, pedregosidad, afloramiento, fragmento_suelo, drenaje_natural, profundidad_efectiva,\n"
                        + "epidedones, endopedones, estado, paisaje, simbolo\n"
                        + "FROM form_comprobacion")) {
                    while (cur.next()) {
                        FormComprobacion row = new FormComprobacion();
                        row.setId(cur.getInt(1));
                        row.setNroObservacion(cur.getString(2));
                        row.setReconocedor(cur.getInt(3));
                        row.setFechaHora(cur.getString(4));
                        row.setLongitud(cur.getDouble(5));
                        row.setLatitud(cur.getDouble(6));
                        row.setAltitud(cur.getDouble(7));
                        row.setNombreSitio(cur.getString(8));
                        row.setEpocaClimatica(cur.getInt(9));
                        row.setDiasLluvia(cur.getString(10));
                        row.setPendienteLongitud(cur.getInt(11));
                        row.setGradoErosion(cur.getInt(12));
                        row.setTipoMovimiento(cur.getInt(13));
                        row.setAnegamiento(cur.getInt(14));
                        row.setFrecuencia(cur.getInt(15));
                        row.setDuracion(cur.getInt(16));
                        row.setPedregosidad(cur.getInt(17));
                        row.setAfloramiento(cur.getInt(18));
                        row.setFragmentoSuelo(cur.getInt(19));
                        row.setDrenajeNatural(cur.getInt(20));
                        row.setProfundidadEfectiva(cur.getInt(21));
                        row.setEpidedones(cur.getInt(22));
                        row.setEndopedones(cur.getInt(23));
                        row.setEstado(cur.getInt(24));
                        row.setPaisaje(cur.getString(25));
                        row.setSimbolo(cur.getString(26));
                        formComprobacionlist.add(row);
                    }
                }

                LOGGER.log(Level.INFO, "ESCOLLECTOR: Leyendo tabla {0}", "form_comprobacion_horizonte");
                formComprobacionHorizonteList = new LinkedList<>();
                try (Statement stmt = connection.createStatement(); ResultSet cur = stmt.executeQuery("SELECT * FROM vw_form_comprobacion_horizonte")) {
                    while (cur.next()) {
                        FormComprobacionHorizonte row = new FormComprobacionHorizonte();
                        row.setId(cur.getInt(1));
                        row.setIdFormComprobacion(cur.getInt(2));
                        row.setNumeroHorizonte(cur.getInt(3));
                        row.setProfundidad(cur.getInt(4));
                        row.setColorHue(cur.getInt(5));
                        row.setColorValue(cur.getInt(6));
                        row.setColorChroma(cur.getInt(7));
                        row.setColorPorcentaje(cur.getInt(8));
                        row.setTipoMaterial(cur.getInt(9));
                        row.setClaseTextural(cur.getInt(10));
                        row.setModificadorTextura(cur.getInt(11));
                        row.setClaseOrganico(cur.getInt(12));
                        row.setClaseComposicion(cur.getInt(13));
                        row.setTexturaPorcentaje(cur.getInt(14));
                        row.setEstructuraTipo(cur.getInt(15));
                        row.setEstructuraClase(cur.getInt(16));
                        row.setEstructuraGrado(cur.getInt(17));
                        row.setFormaRompe(cur.getInt(18));
                        row.setMotivoNoEstructura(cur.getInt(19));
                        row.setHorizonteClase(cur.getInt(20));
                        row.setHorizonteCaracterisitica(cur.getInt(21));
                        row.setTexturaOtro(cur.getString(22));
                        row.setEstructuraOtra(cur.getString(23));
                        row.setDescColorHue(cur.getString(24));
                        row.setDescColorValue(cur.getString(25));
                        row.setDescColorChroma(cur.getString(26));
                        formComprobacionHorizonteList.add(row);
                    }
                }

                LOGGER.log(Level.INFO, "ESCOLLECTOR: Leyendo tabla {0}", "form_comprobacion_moteado");
                formComprobacionHorizonteMotList = new LinkedList<>();
                try (Statement stmt = connection.createStatement(); ResultSet cur = stmt.executeQuery("SELECT * FROM form_comprobacion_moteado")) {
                    while (cur.next()) {
                        FormComprobacionHorizonteOpt row = new FormComprobacionHorizonteOpt();
                        row.setId(cur.getInt(1));
                        row.setIdFormComprobacionHorz(cur.getInt(2));
                        row.setNumeroHorizonte(cur.getInt(3));
                        row.setProfundidad(cur.getInt(4));
                        row.setColorHue(cur.getInt(5));
                        row.setColorValue(cur.getInt(6));
                        row.setColorChroma(cur.getInt(7));
                        row.setColorPorcentaje(cur.getInt(8));
                        row.setTipoMaterial(cur.getInt(9));
                        row.setClaseTextural(cur.getInt(10));
                        row.setModificadorTextura(cur.getInt(11));
                        row.setClaseOrganico(cur.getInt(12));
                        row.setClaseComposicion(cur.getInt(13));
                        row.setTexturaPorcentaje(cur.getInt(14));
                        row.setEstructuraTipo(cur.getInt(15));
                        row.setEstructuraClase(cur.getInt(16));
                        row.setEstructuraGrado(cur.getInt(17));
                        row.setFormaRompe(cur.getInt(18));
                        row.setMotivoNoEstructura(cur.getInt(19));
                        row.setHorizonteClase(cur.getInt(20));
                        row.setHorizonteCaracterisitica(cur.getInt(21));
                        row.setTexturaOtro(cur.getString(22));
                        row.setEstructuraOtra(cur.getString(23));
                        formComprobacionHorizonteMotList.add(row);
                    }
                }

                LOGGER.log(Level.INFO, "ESCOLLECTOR: Leyendo tabla {0}", "form_comprobacion_opcional");
                formComprobacionHorizonteOptList = new LinkedList<>();
                try (Statement stmt = connection.createStatement(); ResultSet cur = stmt.executeQuery("SELECT * FROM form_comprobacion_opcional")) {
                    while (cur.next()) {
                        FormComprobacionHorizonteOpt row = new FormComprobacionHorizonteOpt();
                        row.setId(cur.getInt(1));
                        row.setIdFormComprobacionHorz(cur.getInt(2));
                        row.setNumeroHorizonte(cur.getInt(3));
                        row.setProfundidad(cur.getInt(4));
                        row.setColorHue(cur.getInt(5));
                        row.setColorValue(cur.getInt(6));
                        row.setColorChroma(cur.getInt(7));
                        row.setColorPorcentaje(cur.getInt(8));
                        row.setTipoMaterial(cur.getInt(9));
                        row.setClaseTextural(cur.getInt(10));
                        row.setModificadorTextura(cur.getInt(11));
                        row.setClaseOrganico(cur.getInt(12));
                        row.setClaseComposicion(cur.getInt(13));
                        row.setTexturaPorcentaje(cur.getInt(14));
                        row.setEstructuraTipo(cur.getInt(15));
                        row.setEstructuraClase(cur.getInt(16));
                        row.setEstructuraGrado(cur.getInt(17));
                        row.setFormaRompe(cur.getInt(18));
                        row.setMotivoNoEstructura(cur.getInt(19));
                        row.setHorizonteClase(cur.getInt(20));
                        row.setHorizonteCaracterisitica(cur.getInt(21));
                        row.setTexturaOtro(cur.getString(22));
                        row.setEstructuraOtra(cur.getString(23));
                        formComprobacionHorizonteOptList.add(row);
                    }
                }

                LOGGER.log(Level.INFO, "ESCOLLECTOR: Leyendo tabla {0}", "form_nota_campo");
                formNotaCampoList = new LinkedList<>();
                try (Statement stmt = connection.createStatement(); ResultSet cur = stmt.executeQuery("SELECT id, nro_observacion, reconocedor, fecha_hora, longitud, latitud, altitud,\n"
                        + "nombre_sitio, epoca_climatica, dias_lluvia, gradiente, pendiente_longitud, pendiente_forma,\n"
                        + "clase_erosion, tipo_erosion, grado_erosion, clase_movimiento, tipo_movimiento, frecuencia_movimiento, anegamiento,\n"
                        + "frecuencia, duracion, pedregosidad, afloramiento, vegetacion_natural, grupo_uso, subgrupo_uso,\n"
                        + "nombre_cultivo, observaciones, estado, paisaje, simbolo\n"
                        + "FROM form_nota_campo")) {
                    while (cur.next()) {
                        FormNotaCampo row = new FormNotaCampo();
                        row.setId(cur.getInt(1));
                        row.setNroObservacion(cur.getString(2));
                        row.setReconocedor(cur.getInt(3));
                        row.setFechaHora(cur.getString(4));
                        row.setLongitud(cur.getDouble(5));
                        row.setLatitud(cur.getDouble(6));
                        row.setAltitud(cur.getDouble(7));
                        row.setNombreSitio(cur.getString(8));
                        row.setEpocaClimatica(cur.getInt(9));
                        row.setDiasLluvia(cur.getString(10));
                        row.setGradiente(cur.getInt(11));
                        row.setPendienteLongitud(cur.getInt(12));
                        row.setPendienteForma(cur.getInt(13));
                        row.setClaseErosion(cur.getInt(14));
                        row.setTipoErosion(cur.getInt(15));
                        row.setGradoErosion(cur.getInt(16));
                        row.setClaseMovimiento(cur.getInt(17));
                        row.setTipoMovimiento(cur.getInt(18));
                        row.setFrecuenciaMovimiento(cur.getInt(19));
                        row.setAnegamiento(cur.getInt(20));
                        row.setFrecuencia(cur.getInt(21));
                        row.setDuracion(cur.getInt(22));
                        row.setPedregosidad(cur.getInt(23));
                        row.setAfloramiento(cur.getInt(24));
                        row.setVegetacionNatural(cur.getString(25));
                        row.setGrupoUso(cur.getInt(26));
                        row.setSubgrupoUso(cur.getInt(27));
                        row.setNombreCultivo(cur.getString(28));
                        row.setObservaciones(cur.getString(29));
                        row.setEstado(cur.getInt(30));
                        row.setPaisaje(cur.getString(31));
                        row.setSimbolo(cur.getString(32));
                        formNotaCampoList.add(row);
                    }
                }
            }

        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void writeDatabase(String database) {
        int count;
        try {
            Class.forName("org.postgresql.Driver");
            Properties props = new Properties();
            props.setProperty("user", "postgres");
            props.setProperty("password", "administrator");
            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.0.43:5432/escollectordb", props)) {
                LOGGER.log(Level.INFO, "ESCOLLECTOR: Abriendo base de datos destino");
                String message = "ESCOLLECTOR: Escribiendo en tabla form_comprobacion " + String.valueOf(formComprobacionlist.size()) + " registros";
                LOGGER.log(Level.INFO, message);
                String insertFormComprobacion = "INSERT INTO public.form_comprobacion(\n"
                        + "            nro_observacion, reconocedor, fecha_hora, longitud, latitud, \n"
                        + "            altitud, nombre_sitio, epoca_climatica, dias_lluvia, pendiente_longitud, \n"
                        + "            grado_erosion, tipo_movimiento, anegamiento, frecuencia, duracion, \n"
                        + "            pedregosidad, afloramiento, fragmento_suelo, drenaje_natural, \n"
                        + "            profundidad_efectiva, epidedones, endopedones, estado, paisaje, \n"
                        + "            simbolo)\n"
                        + "    VALUES (?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?)";
                
                PreparedStatement preparedStatement;
                count = 0;
                for (FormComprobacion row : formComprobacionlist) {
                    preparedStatement = connection.prepareStatement(insertFormComprobacion);
                    preparedStatement.setString(1, row.getNroObservacion());
                    preparedStatement.setInt(2, row.getReconocedor());
                    preparedStatement.setDate(3, getDate(row.getFechaHora()));
                    preparedStatement.setDouble(4, row.getLongitud());
                    preparedStatement.setDouble(5, row.getLatitud());
                    preparedStatement.setDouble(6, row.getAltitud());
                    preparedStatement.setString(7, row.getNombreSitio());
                    preparedStatement.setInt(8, row.getEpocaClimatica());
                    preparedStatement.setString(9, row.getDiasLluvia());
                    preparedStatement.setInt(10, row.getPendienteLongitud());
                    preparedStatement.setInt(11, row.getGradoErosion());
                    preparedStatement.setInt(12, row.getTipoMovimiento());
                    preparedStatement.setInt(13, row.getAnegamiento());
                    preparedStatement.setInt(14, row.getFrecuencia());
                    preparedStatement.setInt(15, row.getDuracion());
                    preparedStatement.setInt(16, row.getPedregosidad());
                    preparedStatement.setInt(17, row.getAfloramiento());
                    preparedStatement.setInt(18, row.getFragmentoSuelo());
                    preparedStatement.setInt(19, row.getDrenajeNatural());
                    preparedStatement.setInt(20, row.getProfundidadEfectiva());
                    preparedStatement.setInt(21, row.getEpidedones());
                    preparedStatement.setInt(22, row.getEndopedones());
                    preparedStatement.setInt(23, row.getEstado());
                    preparedStatement.setString(24, row.getPaisaje());
                    preparedStatement.setString(25, row.getSimbolo());

                    try {
                        preparedStatement.executeUpdate();
                        message = "ESCOLLECTOR: Insertado en la base de datos " + database + " en tabla form_comprobacion el NroObservacion " + row.getNroObservacion();
                        LOGGER.log(Level.INFO, message);
                        count++;
                    } catch (PSQLException ex) {
                        if (ex.getSQLState().equals("23505")) {
                            message = "ESCOLLECTOR: Error en la base de datos " + database + " en tabla form_comprobacion ya existe el NroObservacion " + row.getNroObservacion();
                            LOGGER.log(Level.INFO, message);
                        } else {
                            LOGGER.log(Level.INFO, "ESCOLLECTOR: Error en la base de datos {0} en tabla form_comprobacion Error:{1}", new Object[]{database, ex.getMessage()});
                        }
                    }
                }
                
                message = "ESCOLLECTOR: En la base de datos " + database + " total de registros insertados en tabla form_comprobacion " + count;
                LOGGER.log(Level.INFO, message);

                /*String insertFormComprobacionHorizonte = "INSERT INTO public.form_comprobacion_horizonte(\n"
                        + "            nro_observacion, numero_horizonte, profundidad, color_hue, color_value, \n"
                        + "            color_chroma, color_porcentaje, tipo_material, clase_textural, \n"
                        + "            modificador_textura, clase_organico, clase_composicion, textura_porcentaje, \n"
                        + "            estructura_tipo, estructura_clase, estructura_grado, forma_rompe, \n"
                        + "            motivo_no_estructura, horizonte_clase, horizonte_caracterisitica, \n"
                        + "            textura_otro, estructura_otra)\n"
                        + "    VALUES (?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, \n"
                        + "            ?, ?)";
                message = "ESCOLLECTOR: Escribiendo en tabla form_comprobacion_horizonte " + String.valueOf(formComprobacionHorizonteList.size()) + " registros";
                LOGGER.log(Level.INFO, message);
                for (FormComprobacionHorizonte row : formComprobacionHorizonteList) {
                    preparedStatement = connection.prepareStatement(insertFormComprobacionHorizonte);

                    try {
                        preparedStatement.executeUpdate();
                    } catch (PSQLException ex) {
                        if (ex.getSQLState().equals("23505")) {
                            message = "ESCOLLECTOR: Error en la base de datos " + database + " en tabla form_comprobacion_horizonte ya existe el NroObservacion " + row.getNroObservacion() + " con horizonte " + row.getNumeroHorizonte();
                            LOGGER.log(Level.INFO, message);
                        } else {
                            LOGGER.log(Level.INFO, "ESCOLLECTOR: Error en la base de datos {0} en tabla form_comprobacion_horizonte Error:{1}", new Object[]{database, ex.getMessage()});
                        }
                    }
                }*/

                String insertNotaCampo = "INSERT INTO public.form_nota_campo(\n"
                        + "            nro_observacion, reconocedor, fecha_hora, longitud, latitud, \n"
                        + "            altitud, nombre_sitio, epoca_climatica, dias_lluvia, gradiente, \n"
                        + "            pendiente_longitud, pendiente_forma, clase_erosion, tipo_erosion, \n"
                        + "            grado_erosion, clase_movimiento, tipo_movimiento, frecuencia_movimiento, \n"
                        + "            anegamiento, frecuencia, duracion, pedregosidad, afloramiento, \n"
                        + "            vegetacion_natural, grupo_uso, subgrupo_uso, nombre_cultivo, \n"
                        + "            observaciones, estado, paisaje, simbolo)\n"
                        + "    VALUES (?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?)";
                message = "ESCOLLECTOR: Escribiendo en tabla form_nota_campo " + String.valueOf(formNotaCampoList.size()) + " registros";
                LOGGER.log(Level.INFO, message);
                count = 0;
                for (FormNotaCampo row : formNotaCampoList) {
                    preparedStatement = connection.prepareStatement(insertNotaCampo);
                    preparedStatement.setString(1, row.getNroObservacion());
                    preparedStatement.setInt(2, row.getReconocedor());
                    preparedStatement.setDate(3, getDate(row.getFechaHora()));
                    preparedStatement.setDouble(4, row.getLongitud());
                    preparedStatement.setDouble(5, row.getLatitud());
                    preparedStatement.setDouble(6, row.getAltitud());
                    preparedStatement.setString(7, row.getNombreSitio());
                    preparedStatement.setInt(8, row.getEpocaClimatica());
                    preparedStatement.setString(9, row.getDiasLluvia());
                    preparedStatement.setInt(10, row.getGradiente());
                    preparedStatement.setInt(11, row.getPendienteLongitud());
                    preparedStatement.setInt(12, row.getPendienteForma());
                    preparedStatement.setInt(13, row.getClaseErosion());
                    preparedStatement.setInt(14, row.getTipoErosion());
                    preparedStatement.setInt(15, row.getGradoErosion());
                    preparedStatement.setInt(16, row.getClaseMovimiento());
                    preparedStatement.setInt(17, row.getTipoMovimiento());
                    preparedStatement.setInt(18, row.getFrecuenciaMovimiento());
                    preparedStatement.setInt(19, row.getAnegamiento());
                    preparedStatement.setInt(20, row.getFrecuencia());
                    preparedStatement.setInt(21, row.getDuracion());
                    preparedStatement.setInt(22, row.getPedregosidad());
                    preparedStatement.setInt(23, row.getAfloramiento());
                    preparedStatement.setString(24, row.getVegetacionNatural());
                    preparedStatement.setInt(25, row.getGrupoUso());
                    preparedStatement.setInt(26, row.getSubgrupoUso());
                    preparedStatement.setString(27, row.getNombreCultivo());
                    preparedStatement.setString(28, row.getObservaciones());
                    preparedStatement.setInt(29, row.getEstado());
                    preparedStatement.setString(30, row.getPaisaje());
                    preparedStatement.setString(31, row.getSimbolo());

                    try {
                        preparedStatement.executeUpdate();
                        message = "ESCOLLECTOR: Insertado en la base de datos " + database + " en tabla form_nota_campo el NroObservacion " + row.getNroObservacion();
                        LOGGER.log(Level.INFO, message);
                        count++;
                    } catch (PSQLException ex) {
                        if (ex.getSQLState().equals("23505")) {
                            message = "ESCOLLECTOR: Error en la base de datos " + database + " en tabla form_nota_campo ya existe el NroObservacion " + row.getNroObservacion();
                            LOGGER.log(Level.INFO, message);
                        } else {
                            LOGGER.log(Level.INFO, "ESCOLLECTOR: Error en la base de datos {0} en tabla form_nota_campo Error:{1}", new Object[]{database, ex.getMessage()});
                        }
                    }
                }

                message = "ESCOLLECTOR: En la base de datos " + database + " total de registros insertados en tabla form_nota_campo " + count;
                LOGGER.log(Level.INFO, message);
            }

        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public static Date getDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            java.util.Date date = formatter.parse(strDate);
            return new Date(date.getTime());
        } catch (Exception ex) {
            return null;
        }
    }

    public static void main(String args[]) {
        Principal principal = new Principal();
        principal.loadFiles();
    }

}
