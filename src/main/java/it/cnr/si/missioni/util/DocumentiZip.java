/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package it.cnr.si.missioni.util;

import it.cnr.si.missioni.cmis.CMISMissioniAspect;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DocumentiZip {

    private static final Logger log = LoggerFactory.getLogger(DocumentiZip.class);

    private final Map<String, String> documenti = new LinkedHashMap<>();
    private final Map<String, StorageObject> oggetti = new HashMap<>();
    private final Map<String, byte[]> file = new LinkedHashMap<>();
    private final Set<String> cartelle = new LinkedHashSet<>();

    public void aggiungi(String cartella, StorageObject documento) {
        if (documento != null && !documenti.containsKey(documento.getKey())) {
            documenti.put(documento.getKey(), cartella(cartella));
            oggetti.put(documento.getKey(), documento);
        }
    }

    public void aggiungiFile(String cartella, String nome, byte[] contenuto) {
        file.put(cartella(cartella) + nome, contenuto);
    }

    public void aggiungiCartella(String cartella) {
        cartelle.add(cartella(cartella));
    }

    // i nomi duplicati prendono un progressivo, es. "allegato (2).pdf"
    public void scrivi(OutputStream outputStream, Function<StorageObject, InputStream> contenuto) throws IOException {
        Set<String> percorsi = new HashSet<>();
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (Map.Entry<String, byte[]> f : file.entrySet()) {
                creaCartella(zos, f.getKey().substring(0, f.getKey().lastIndexOf('/') + 1), percorsi);
                zos.putNextEntry(new ZipEntry(percorsoUnivoco(f.getKey(), percorsi)));
                zos.write(f.getValue());
                zos.closeEntry();
            }
            for (Map.Entry<String, String> d : documenti.entrySet()) {
                StorageObject documento = oggetti.get(d.getKey());
                String nome = documento.getPropertyValue(StoragePropertyNames.NAME.value());
                try (InputStream is = contenuto.apply(documento)) {
                    if (nome == null || is == null) {
                        continue;
                    }
                    creaCartella(zos, d.getValue(), percorsi);
                    zos.putNextEntry(new ZipEntry(percorsoUnivoco(d.getValue() + nome, percorsi)));
                    IOUtils.copyLarge(is, zos);
                    zos.closeEntry();
                } catch (RuntimeException e) {
                    log.warn("File {} non aggiunto allo zip", nome, e);
                }
            }
            for (String cartella : cartelle) {
                creaCartella(zos, cartella, percorsi);
            }
        }
    }

    public static byte[] unisciPdf(List<InputStream> pdf) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        pdf.forEach(merger::addSource);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        merger.setDestinationStream(out);
        try {
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        } finally {
            pdf.forEach(IOUtils::closeQuietly);
        }
        return out.toByteArray();
    }

    public static boolean haAspetto(StorageObject documento, String aspetto) {
        Object aspetti = documento.getPropertyValue(StoragePropertyNames.SECONDARY_OBJECT_TYPE_IDS.value());
        if (aspetti == null) {
            return false;
        }
        Collection<?> lista = aspetti instanceof Collection ? (Collection<?>) aspetti
                : Arrays.asList(aspetti.toString().replace("[", "").replace("]", "").split(","));
        return lista.stream().anyMatch(a -> a != null && aspetto.equals(a.toString().trim()));
    }

    public static boolean haUnoDegliAspetti(StorageObject documento, Collection<String> aspetti) {
        return aspetti.stream().anyMatch(a -> haAspetto(documento, a));
    }

    public static boolean isEliminato(StorageObject documento) {
        Object nome = documento.getPropertyValue(StoragePropertyNames.NAME.value());
        return haAspetto(documento, CMISMissioniAspect.FILE_ELIMINATO.value())
                || (nome != null && nome.toString().endsWith(".eliminato"));
    }

    public static boolean isCartella(StorageObject documento) {
        Object baseType = documento.getPropertyValue(StoragePropertyNames.BASE_TYPE_ID.value());
        if (baseType != null) {
            return StoragePropertyNames.CMIS_FOLDER.value().equals(baseType);
        }
        Object tipo = documento.getPropertyValue(StoragePropertyNames.OBJECT_TYPE_ID.value());
        return tipo != null && (StoragePropertyNames.CMIS_FOLDER.value().equals(tipo) || tipo.toString().startsWith("F:"));
    }

    private static String cartella(String cartella) {
        if (cartella == null || cartella.trim().isEmpty()) {
            return "";
        }
        String c = cartella.trim().replace('\\', '/');
        return c.endsWith("/") ? c : c + "/";
    }

    private static void creaCartella(ZipOutputStream zos, String cartella, Set<String> percorsi) throws IOException {
        if (!cartella.isEmpty() && percorsi.add(cartella)) {
            zos.putNextEntry(new ZipEntry(cartella));
            zos.closeEntry();
        }
    }

    private static String percorsoUnivoco(String percorso, Set<String> percorsi) {
        int punto = percorso.lastIndexOf('.');
        boolean conEstensione = punto > percorso.lastIndexOf('/');
        String base = conEstensione ? percorso.substring(0, punto) : percorso;
        String estensione = conEstensione ? percorso.substring(punto) : "";
        String candidato = percorso;
        for (int i = 2; !percorsi.add(candidato); i++) {
            candidato = base + " (" + i + ")" + estensione;
        }
        return candidato;
    }
}
