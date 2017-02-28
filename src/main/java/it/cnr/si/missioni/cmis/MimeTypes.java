package it.cnr.si.missioni.cmis;

public enum MimeTypes
{
    BMP ("image/bmp"),
    XLS ("application/vnd.ms-excel"),
    XLSX ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    DOC ("application/msword"),
    DOCX ("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    JPG ("image/jpeg"),
    JPEG ("image/pjpeg"),
    TIFF ("image/tiff"),
    PNG ("image/png"),
    GZ ("application/x-gzip"),
    TAR ("application/x-tar"),
    ZIP ("application/zip"),
    ZIP_2 ("application/x-compressed-zip"),
    HTML ("text/html"),
    XHTML ("text/xhtml"),
    TEXT ("text/plain"),
    JAVASCRIPT ("text/javascript"),
    APP_XML ("application/xml"),
    XML ("text/xml"),
    PDF ("application/pdf"),
    ATOM ("application/atom+xml"),
    ATOMFEED ("application/atom+xml;type=feed"),
    ATOMENTRY ("application/atom+xml;type=entry"),
    FORMDATA ("multipart/form-data"),
    JSON ("application/json");
    
    private String mimetype;

    MimeTypes(String mimetype)
    {
        this.mimetype = mimetype;
    }
    
    public String mimetype()
    {
        return mimetype;
    }
}