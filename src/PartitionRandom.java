import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PartitionRandom extends Partition {

    HashSet<String> interactions;

    /*//constructor for reading matrix file
    public PartitionRandom(String filepath) {
        this.interactions = new HashSet<>();
        ArrayList<String> data = readFromFile(filepath);
        String[] split = data.get(0).split("\t");
        for(int i = 1;i < split.length;i ++)
            this.interactions.add(split[i]);
    }*/

    //constructor for reading list file, id is in index 1,2
    public PartitionRandom(String filepath) {
        this.interactions = new HashSet<>();
        //ArrayList<String> data = readFromFile(filepath);
        String[] tempsplit;
        for(String s:readFromFile(filepath)){
            tempsplit = s.split("\t");
            this.interactions.add(tempsplit[1]);
            this.interactions.add(tempsplit[2]);
        }
    }

    /*//constructor for reading list file, id is in index 0
    public PartitionRandom(String filepath) {
        this.interactions = new HashSet<>();
        ArrayList<String> data = readFromFile(filepath);
        for(String s:data)
            this.interactions.add(s.split(",")[0]);
    }*/

    public ArrayList<String> readFromFile(String filepath) {
        ArrayList returnList = new ArrayList<>();
        Path path = FileSystems.getDefault().getPath(filepath);
        try{
            InputStream in = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() != 0)
                    returnList.add(line);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return returnList;
    }

    @Override
    public HashSet<String>[] partition(double capacity,int servers){
        HashSet<String>[] partitions = new HashSet[servers];
        for(int i = 0;i < partitions.length;i ++)
            partitions[i] = new HashSet<>();
        String id;
        Random random = new Random();
        Iterator<String> iterator = this.interactions.iterator();
        while(iterator.hasNext()) {
            id = iterator.next();
            partitions[random.nextInt(servers)].add(id);
        }
        return partitions;
    }

    /*//@Override
    public void partition(double capacity) {
        return;
    }

    //@Override
    public HashSet<String>[] assign(int serverNumber) {
        HashSet<String>[] partitions = new HashSet[serverNumber];
        for(int i = 0;i < partitions.length;i ++)
            partitions[i] = new HashSet<>();
        String id;
        Random random = new Random();
        Iterator<String> iterator = this.interactions.iterator();
        while(iterator.hasNext()) {
            id = iterator.next();
            partitions[random.nextInt(partitions.length)].add(id);
        }
        return partitions;
    }*/

    /*@Override
    public HashSet<String>[] partition(double[] capacities) {
        HashSet<String>[] partitions = new HashSet[capacities.length];
        for(int i = 0;i < partitions.length;i ++)
            partitions[i] = new HashSet<>();
        String id;
        Random random = new Random();
        Iterator<String> iterator = this.interactions.iterator();
        while(iterator.hasNext()) {
            id = iterator.next();
            //this.ids.remove(id);
            partitions[random.nextInt(partitions.length)].add(id);
        }
        return partitions;
    }*/

}
