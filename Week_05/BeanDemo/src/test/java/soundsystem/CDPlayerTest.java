package soundsystem;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = CDPlayerConfig.class)
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml"})
public class CDPlayerTest {

    @Rule
    public final SystemOutRule log = new SystemOutRule().enableLog();

    @Autowired
    private CompactDisc cd;

    @Autowired
    private MediaPlayer player;

    @Test
    public void cdShouldNotBeNull() {
        assertNotNull(cd);
    }

    @Test
    public void playByAutowired() {
        player.play();
        assertEquals("正在播放：周杰伦的范特西\r\n", log.getLog());
    }

    @Test
    public void playByJavaConfig() {
        player.play();
        assertEquals("正在播放：周杰伦的周杰伦\r\n", log.getLog());
    }

    @Test
    public void playByXML() {
        player.play();
        assertEquals("正在播放：周杰伦的七里香\r\n", log.getLog());
    }
}