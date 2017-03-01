package drinkwater.rest.fileupload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.File;

public class FileUploadProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ExchangeFileUpload upload = new ExchangeFileUpload(factory);

        java.util.List<FileItem> items = upload.parseExchange(exchange);

        if(items.size() >= 1){
            exchange.getIn().setBody(items.get(0).getInputStream());

            for (int i = 1; i < items.size(); i++) {
                exchange.setProperty(items.get(i).getName(), items.get(i).getInputStream());
            }
        }
    }
}
