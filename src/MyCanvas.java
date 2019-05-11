import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MyCanvas extends JPanel {

    private int[][][] coordinates;
    private static Color[] colors = new Color[]{Color.black,Color.blue,Color.cyan/*,Color.darkGray,Color.gray*/,Color.green/*,Color.lightGray*/,Color.magenta,Color.orange,Color.pink,Color.red,Color.white,Color.yellow};
    private int width;
    private int height;
    private int scale;

    public MyCanvas(Set<String>[] partitions,int scale){
        this.width = this.calculateWidth(partitions);
        this.height = this.calculateHeight(partitions);
        Set<String>[] zeroedPartitions = this.translate(partitions,- 1);
        int temp,right = 99,bottom = 99;
        for(Set<String> partition:zeroedPartitions)
            for(String s:partition) {
                temp = Integer.parseInt(s) % 100;
                if(right > temp)
                    right = temp;
                temp = Integer.parseInt(s) / 100;
                if(bottom > temp)
                    bottom = temp;
            }
        zeroedPartitions = this.translate(this.translate(zeroedPartitions,- bottom * 100),- right);
        this.coordinates = new int[partitions.length][][];
        Iterator<String> iterator;
        String entry;
        for(int i = 0;i < partitions.length;i ++){
            this.coordinates[i] = new int[partitions[i].size()][2];
            iterator = zeroedPartitions[i].iterator();
            for(int j = 0;j < zeroedPartitions[i].size();j ++){
                entry = iterator.next();
                this.coordinates[i][j][0] = Integer.parseInt(entry) % 100;
                this.coordinates[i][j][1] = this.height - Integer.parseInt(entry) / 100 - 1;
            }
        }
        if(colors.length < partitions.length) {
            colors = new Color[partitions.length];
            //Random random = new Random();
            for(int i = 0;i < colors.length;i ++)
                colors[i] = new Color((int)(Math.random() * 0x1000000));
        }
        this.scale = scale;
    }

    private Set<String>[] translate(Set<String>[] sets,int distance) {
        Set<String>[] newSets = new Set[sets.length];
        for(int i = 0;i < sets.length;i ++) {
            newSets[i] = new HashSet<>();
            for(String s:sets[i])
                newSets[i].add(Integer.toString(Integer.parseInt(s) + distance));
        }
        return newSets;
    }

    private int calculateWidth(Set<String>[] partitions) {
        int min = 99,max = 0,temp;
        for(Set<String> partition:partitions)
            for(String s:partition) {
                temp = Integer.parseInt(s) % 100;
                if(min > temp)
                    min = temp;
                if(max < temp)
                    max = temp;
            }
        return max - min + 1;
    }

    private int calculateHeight(Set<String>[] partitions) {
        int min = 99,max = 0,temp;
        for(Set<String> partition:partitions)
            for(String s:partition) {
                temp = Integer.parseInt(s) / 100;
                if(min > temp)
                    min = temp;
                if(max < temp)
                    max = temp;
            }
        return max - min + 1;
    }

    @Override
    public void paint(Graphics g){
        for(int i = 0;i < this.coordinates.length;i ++){
            g.setColor(this.colors[i]);
            for(int[] ints:this.coordinates[i])
                g.fillRect(ints[0] * this.scale,ints[1] * this.scale,this.scale,this.scale);
        }
    }

}
