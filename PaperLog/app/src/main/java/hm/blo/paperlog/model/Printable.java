package hm.blo.paperlog.model;

/**
 * Interface of a class that can provide data to the printer.
 */
public interface Printable {
    String getPrintString();
    boolean hasContent();
}
