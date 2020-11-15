package soundsystem;

import org.springframework.stereotype.Component;


@Component
public class Fantasy implements CompactDisc {

    private String title = "范特西";
    private String artist = "周杰伦";

    @Override
    public void play() {
        System.out.println("正在播放：" + artist + "的" + title);
    }
}
