import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PartitionNormalMu extends Partition<DistributionNormalMuComparable> {

    private double theta = 1;

    public PartitionNormalMu(String filepath) {
        HashMap<Pair<String>,HashMap<Long,Double>> map = new HashMap<>();
        Long min = Long.MAX_VALUE,max = Long.MIN_VALUE,time;
        String[] tempsplit;
        //ArrayList<Double> templist;
        HashMap<Long,Double> tempmap;
        for(String s:this.readFromFile(filepath)) {
            tempsplit = s.split("\t");
            /*templist = new ArrayList<>();
            templist.add(Double.parseDouble(tempsplit[3]));
            map.merge(new Pair<>(tempsplit[1],tempsplit[2]),templist,(oldlist,newlist) -> {
                oldlist.addAll(newlist);
                return oldlist;
            });*/
            if(map.containsKey(new Pair<>(tempsplit[1],tempsplit[2])))
                map.get(new Pair<>(tempsplit[1],tempsplit[2])).merge(Long.parseLong(tempsplit[0]),Double.parseDouble(tempsplit[3]),Double::sum);
            else {
                tempmap = new HashMap<>();
                tempmap.put(Long.parseLong(tempsplit[0]),Double.parseDouble(tempsplit[3]));
                map.put(new Pair<>(tempsplit[1],tempsplit[2]),tempmap);
            }
            time = Long.parseLong(tempsplit[0]);
            if(max < time)
                max = time;
            if(min > time)
                min = time;
        }
        time = (max - min) / 600000 + 1;
        Map.Entry<Pair<String>,HashMap<Long,Double>> tempentry;
        Iterator<Map.Entry<Pair<String>,HashMap<Long,Double>>> iterator = map.entrySet().iterator();
        this.interactions = new TreeSet<>(new SuperCellPairComparator0());
        while (iterator.hasNext()) {
            tempentry = iterator.next();
            this.interactions.add(new SuperCellPair<>(tempentry.getKey().id1, tempentry.getKey().id2, new DistributionNormalMuComparable(tempentry.getValue().values(),time), new DistributionNormalMuComparable(map.get(new Pair<>(tempentry.getKey().id1, tempentry.getKey().id1)).values(),time), new DistributionNormalMuComparable(map.get(new Pair<>(tempentry.getKey().id2, tempentry.getKey().id2)).values(),time)));
            /*if (tempentry.getKey().id1.equals(tempentry.getKey().id2))
                this.interactions.add(new SuperCellPair<>(tempentry.getKey().id1, tempentry.getKey().id2, new DistributionNormalMuComparable(tempentry.getValue()), new DistributionNormalMuComparable(tempentry.getValue()), new DistributionNormalMuComparable(tempentry.getValue())));
            else
                this.interactions.add(new SuperCellPair<>(tempentry.getKey().id1, tempentry.getKey().id2, new DistributionNormalMuComparable(tempentry.getValue()), new DistributionNormalMuComparable(map.get(new Pair<>(tempentry.getKey().id1, tempentry.getKey().id1))), new DistributionNormalMuComparable(map.get(new Pair<>(tempentry.getKey().id2, tempentry.getKey().id2)))));*/
        }
    }

    /*//constructor for reading matrix format
    public PartitionNormalMu(String filepath) {
        this.interactions = new TreeSet<>(new SuperCellPairComparator0());
        String[][][] matrix = SampleDandelionAreaToArea.importMatrix(filepath);
        ArrayList<Double> ijList,iiList,jjList;
        for(int i = 1;i < matrix[0].length;i ++)
            for(int j = i;j < matrix[0].length;j ++){
                ijList = new ArrayList<>();
                iiList = new ArrayList<>();
                jjList = new ArrayList<>();
                for(int k = 0;k < matrix.length;k ++) {
                    ijList.add(Double.parseDouble(matrix[k][i][j]));
                    iiList.add(Double.parseDouble(matrix[k][i][i]));
                    jjList.add(Double.parseDouble(matrix[k][j][j]));
                }
                if(!ijList.isEmpty())
                    this.interactions.add(new SuperCellPair<>(matrix[0][i][0], matrix[0][0][j],new DistributionNormalMuComparable(ijList),new DistributionNormalMuComparable(iiList),new DistributionNormalMuComparable(jjList)));
                if(!iiList.isEmpty())
                    this.interactions.add(new SuperCellPair<>(matrix[0][i][0],matrix[0][i][0],new DistributionNormalMuComparable(iiList),new DistributionNormalMuComparable(iiList),new DistributionNormalMuComparable(iiList)));
                if(!jjList.isEmpty())
                    this.interactions.add(new SuperCellPair<>(matrix[0][0][j],matrix[0][0][j],new DistributionNormalMuComparable(jjList),new DistributionNormalMuComparable(jjList),new DistributionNormalMuComparable(jjList)));
            }
    }*/

    public void setTheta(double theta){
        this.theta = theta;
    }

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

    private boolean isMerging(double capacity) {
        Iterator<SuperCellPair<DistributionNormalMuComparable>> iterator = this.interactions.descendingIterator();
        SuperCellPair<DistributionNormalMuComparable> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (!entry.pair.id1.equals(entry.pair.id2) && DistributionNormal.merge(DistributionNormal.merge(entry.ij, entry.ii), entry.jj).cdf(capacity) >= this.theta) {
                this.interactions = this.merge(entry);
                return true;
            }
        }
        return false;
    }

    private TreeSet<SuperCellPair<DistributionNormalMuComparable>> merge(SuperCellPair<DistributionNormalMuComparable> tobeMerged) {
        String mergedId = tobeMerged.pair.id1 + "," + tobeMerged.pair.id2;
        DistributionNormalMuComparable mergedValue = new DistributionNormalMuComparable(tobeMerged.ij.mu + tobeMerged.ii.mu + tobeMerged.jj.mu,tobeMerged.ij.sigmasquare + tobeMerged.ii.sigmasquare + tobeMerged.jj.sigmasquare);
        //update TreeSet
        TreeSet<SuperCellPair<DistributionNormalMuComparable>> interactionSet = new TreeSet<>(new SuperCellPairComparator0());
        //add merged super cell
        interactionSet.add(new SuperCellPair<>(mergedId,mergedId,mergedValue,mergedValue,mergedValue));
        //because treeset can not be used to look for objects to be updated, temporary map is used to store updating objects to be added into treeset later
        HashMap<String,SuperCellPair<DistributionNormalMuComparable>> tempMap = new HashMap<>();
        SuperCellPair<DistributionNormalMuComparable> tempEntry;
        for(SuperCellPair<DistributionNormalMuComparable> entry:this.interactions) {
            //if entry has none in the merging
            if(!tobeMerged.pair.contains(entry.pair.id1) && !tobeMerged.pair.contains(entry.pair.id2)) {
                interactionSet.add(entry);
                continue;
            }
            //if entry id1 is in the merging, and not id2
            if (tobeMerged.pair.contains(entry.pair.id1) && !tobeMerged.pair.contains(entry.pair.id2)) {
                tempEntry = new SuperCellPair<>(mergedId,entry.pair.id2,entry.ij,mergedValue,entry.jj);
                tempMap.merge(entry.pair.id2,tempEntry,(SuperCellPair<DistributionNormalMuComparable> oldEntry, SuperCellPair<DistributionNormalMuComparable> newEntry) -> {
                    newEntry = new SuperCellPair<>(newEntry.pair.id1,newEntry.pair.id2,new DistributionNormalMuComparable(oldEntry.ij.mu + newEntry.ij.mu,oldEntry.ij.sigmasquare + newEntry.ij.sigmasquare),newEntry.ii,newEntry.jj);
                    return newEntry;
                });
                continue;
            }
            //if entry id2 is in the merging, and not id1
            if (!tobeMerged.pair.contains(entry.pair.id1) && tobeMerged.pair.contains(entry.pair.id2)) {
                tempEntry = new SuperCellPair<>(entry.pair.id1,mergedId,entry.ij,entry.ii,mergedValue);
                tempMap.merge(entry.pair.id1,tempEntry,(SuperCellPair<DistributionNormalMuComparable> oldEntry, SuperCellPair<DistributionNormalMuComparable> newEntry) -> {
                    newEntry = new SuperCellPair<>(newEntry.pair.id1,newEntry.pair.id2,new DistributionNormalMuComparable(oldEntry.ij.mu + newEntry.ij.mu,oldEntry.ij.sigmasquare + newEntry.ij.sigmasquare),newEntry.ii,newEntry.jj);
                    return newEntry;
                });
                continue;
            }
        }
        interactionSet.addAll(tempMap.values());
        return interactionSet;
    }

    /*@Override
    public void partition(double capacity){
        while(this.isMerging(capacity));
    }

    @Override
    public HashSet<String>[] assign(int serverNumber){
        //initialize partition for return
        HashSet<String>[] partitions = new HashSet[serverNumber];
        Arrays.fill(partitions,new HashSet<>());
        int index = 0;
        //iterate over partitions
        Iterator<SuperCellPair<DistributionNormalMuComparable>> iterator = this.interactions.descendingIterator();
        SuperCellPair<DistributionNormalMuComparable> interactionEntry;
        while(iterator.hasNext() && index < serverNumber) {
            interactionEntry = iterator.next();
            if (interactionEntry.pair.id1.equals(interactionEntry.pair.id2)) {
                partitions[index] = new HashSet<>(Arrays.asList(interactionEntry.pair.id1.split(",")));
                index ++;
            }
        }
        return partitions;
    }*/

    @Override
    public HashSet<String>[] partition(double capacity,int serverNumber) {
        while(this.isMerging(capacity));
        //initialize partition for return
        HashSet<String>[] partitions = new HashSet[serverNumber];
        Arrays.fill(partitions,new HashSet<>());
        int index = 0;
        //iterate over partitions
        Iterator<SuperCellPair<DistributionNormalMuComparable>> iterator = this.interactions.descendingIterator();
        SuperCellPair<DistributionNormalMuComparable> interactionEntry;
        while(iterator.hasNext() && index < serverNumber) {
            interactionEntry = iterator.next();
            if (interactionEntry.pair.id1.equals(interactionEntry.pair.id2)) {
                partitions[index] = new HashSet<>(Arrays.asList(interactionEntry.pair.id1.split(",")));
                index ++;
            }
        }
        return partitions;
    }

}
