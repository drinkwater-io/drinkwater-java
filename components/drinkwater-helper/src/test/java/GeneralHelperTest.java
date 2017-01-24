import drinkwater.helper.GeneralHelper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneralHelperTest {

    @Test
    public void shouldGetCurrentFolder(){
       String currentFolder =  GeneralHelper.getJarFolder();

       assertThat(currentFolder).isNotNull();
    }
}
