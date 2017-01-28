import drinkwater.helper.GeneralUtils;
import org.junit.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneralUtilsTest {

    @Test
    public void shouldGetCurrentFolder(){
       Path currentFolder =  GeneralUtils.getJarFolderPath(null);

       assertThat(currentFolder).isNotNull();
    }
}
