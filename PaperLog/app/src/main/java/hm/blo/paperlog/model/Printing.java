package hm.blo.paperlog.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hm.blo.paperlog.MainActivity;
import hm.blo.paperlog.R;
import hm.blo.paperlog.ui.main.LatLongViewModel;



/**
 * Singleton for printing infrastructure.
 * Using Bluetooth option of https://github.com/DantSu/ESCPOS-ThermalPrinter-Android
 */
public class Printing {

    private static ReportPrintError errorReporter = (error) -> {};

    private static List<Printable> printables = new ArrayList<Printable>();

    public interface SetDeviceName {
        void setDeviceName(String deviceName);
    }

    public interface ReportPrintError {
        void report(String printError);
    }

    public static void addPrintable(Printable printable){
        if (printable instanceof LatLongViewModel) {
            Printing.printables.add(0,printable);
        } else {
            Printing.printables.add(printable);
        }
    }

    public static void SetReportPrintError(ReportPrintError errorReporter) {
        Printing.errorReporter = errorReporter;
    }

    public static void printPrintables() {
        if (selectedDevice != null) {
            try {
                EscPosPrinter printer = new EscPosPrinter(selectedDevice, 203, 48f, 32);
                StringBuilder toOutput = new StringBuilder();
                for (Printable printable: printables) {
                    if (printable.hasContent()) {
                        toOutput.append(printable.getPrintString());
                        toOutput.append("\n");
                    }
                }

                printer.printFormattedText(toOutput.toString());

            } catch (EscPosConnectionException e) {
                errorReporter.report(e.toString());
            } catch (EscPosBarcodeException e) {
                errorReporter.report(e.toString());
            } catch (EscPosEncodingException e) {
                errorReporter.report(e.toString());
            } catch (EscPosParserException e) {
                errorReporter.report(e.toString());
            }
        }
    }

    public static void printStringNow(String s) {
        if (selectedDevice != null) {
            try {
                EscPosPrinter printer = new EscPosPrinter(selectedDevice, 203, 48f, 32);
                printer.printFormattedText(s);
            } catch (EscPosConnectionException e) {
                errorReporter.report(e.toString());
            } catch (EscPosBarcodeException e) {
                errorReporter.report(e.toString());
            } catch (EscPosEncodingException e) {
                errorReporter.report(e.toString());
            } catch (EscPosParserException e) {
                errorReporter.report(e.toString());
            }
        }
    }



    // TODO: print job queue

    public static void permissionReceived() {
    }

    private static BluetoothConnection selectedDevice;

    public static void browseBluetoothDevice(Context context, SetDeviceName setDeviceName) {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
            items[0] = "No printer";
            int i = 0;
            try {
                for (BluetoothConnection device : bluetoothDevicesList) {
                    items[++i] = device.getDevice().getName();
                }
            } catch (SecurityException e)
            {
                // getName requires permission, likely this is thrown when not granted
                items[1] = "Permission required";
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Bluetooth printer selection");
            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int index = i - 1;
                    if(index == -1) {
                        selectedDevice = null;
                    } else {
                        selectedDevice = bluetoothDevicesList[index];
                    }
                    setDeviceName.setDeviceName(items[i]);
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            setDeviceName.setDeviceName("<not selected>");
            alert.show();

        }
    }

    public static boolean TryLoadPrinterByName(String printerName) {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
        for (BluetoothConnection device : bluetoothDevicesList) {
            try {
                if (device.getDevice().getName().equals(printerName)) {
                    selectedDevice = device;
                    return true;
                }
            } catch (SecurityException e)
            {
                // getName requires permission, likely this is thrown when not granted
                return false;
            }

        }

        return false;
    }
}
