import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PartitionMECGeoAverage extends Partition<Double> {

    private HashSet<Pair<String>> neighborSet;

    public PartitionMECGeoAverage(String filepath) {
        HashMap<Pair<String>,Double> tempmap = new HashMap<>();
        Long min = Long.MAX_VALUE,max = Long.MIN_VALUE,time;
        String[] tempsplit;
        for(String s:this.readFromFile(filepath)){
            tempsplit = s.split("\t");
            tempmap.merge(new Pair<>(tempsplit[1],tempsplit[2]),Double.parseDouble(tempsplit[3]),Double::sum);
            time = Long.parseLong(tempsplit[0]);
            if(max < time)
                max = time;
            if(min > time)
                min = time;
        }
        time = (max - min) / 600000 + 1;
        Map.Entry<Pair<String>,Double> tempentry;
        Iterator<Map.Entry<Pair<String>,Double>> iterator = tempmap.entrySet().iterator();
        this.interactions = new TreeSet<>(new SuperCellPairComparator0());
        while (iterator.hasNext()) {
            tempentry = iterator.next();
            this.interactions.add(new SuperCellPair<>(tempentry.getKey().id1,tempentry.getKey().id2,tempentry.getValue() / time,tempmap.get(new Pair<>(tempentry.getKey().id1,tempentry.getKey().id1)) / time,tempmap.get(new Pair<>(tempentry.getKey().id2,tempentry.getKey().id2)) / time));
            /*if(tempentry.getKey().id1.equals(tempentry.getKey().id2))
                this.interactions.add(new SuperCellPair<>(tempentry.getKey().id1,tempentry.getKey().id2,tempentry.getValue() / time,tempentry.getValue() / time,tempentry.getValue() / time));
            else
                this.interactions.add(new SuperCellPair<>(tempentry.getKey().id1,tempentry.getKey().id2,tempentry.getValue() / time,tempmap.get(new Pair<>(tempentry.getKey().id1,tempentry.getKey().id1)) / time,tempmap.get(new Pair<>(tempentry.getKey().id2,tempentry.getKey().id2)) / time));*/
        }
        this.neighborSet = this.createNeighborSet(this.interactions);
    }

    //constructor for reading matrix format
    /*public PartitionMECGeoAverage(String filepath) {
        this.interactions = new TreeSet<>(new SuperCellPairComparator0());
        String[][][] matrix = SampleDandelionAreaToArea.importMatrix(filepath);
        double averageIJ,averageII,averageJJ;
        for(int i = 1;i < matrix[0].length;i ++)
            for(int j = i;j < matrix[0].length;j ++){
                averageIJ = 0;
                averageII = 0;
                averageJJ = 0;
                for(int k = 0;k < matrix.length;k ++) {
                    averageIJ += Double.parseDouble(matrix[k][i][j]);
                    averageII += Double.parseDouble(matrix[k][i][i]);
                    averageJJ += Double.parseDouble(matrix[k][j][j]);
                }
                if(averageIJ != 0)
                    this.interactions.add(new SuperCellPair(matrix[0][i][0], matrix[0][0][j], averageIJ / matrix.length, averageII / matrix.length, averageJJ / matrix.length));
                if(averageII != 0)
                    this.interactions.add(new SuperCellPair(matrix[0][i][0],matrix[0][i][0],averageII / matrix.length,averageII / matrix.length,averageII / matrix.length));
                if(averageJJ != 0)
                    this.interactions.add(new SuperCellPair(matrix[0][0][j],matrix[0][0][j],averageJJ / matrix.length,averageJJ / matrix.length,averageJJ / matrix.length));
            }
        this.neighborSet = this.createNeighborSet(this.interactions);
    }*/

    //create neighborset from pair of ids in Milano
    private HashSet<Pair<String>> createNeighborSet(Set<SuperCellPair<Double>> keySet) {
        HashSet<Pair<String>> neighborSet = new HashSet<>();
        int id1,id2,matrixSize = 100;
        for(SuperCellPair<Double> interaction:keySet) {
            if (!(interaction.pair.id1.equals(interaction.pair.id2) || neighborSet.contains(new Pair<>(interaction.pair.id1,interaction.pair.id2)))) {
                id1 = Integer.parseInt(interaction.pair.id1);
                id2 = Integer.parseInt(interaction.pair.id2);
                if (id2 >= id1 - matrixSize - 1 && id2 <= id1 - matrixSize + 1 || id2 == id1 - 1 || id2 == id1 + 1 || id2 >= id1 + matrixSize - 1 && id2 <= id1 + matrixSize + 1)
                    neighborSet.add(new Pair<>(interaction.pair.id1,interaction.pair.id2));
            }
        }
        return neighborSet;
    }

    /*//create neightborset from mapfile, obsolete
    private HashSet<String> createNeighborSet(String mapFile) {
        HashSet<String> neighborSet = new HashSet<>();
        ArrayList<String> tempData = this.readFromFile(mapFile);
        String[] tempSplit;
        //check if the mapfile is a geojson file, if not, proceed as if it is a neighbor matrix file
        if (mapFile.endsWith(".geojson")) {
            String tempString = "";
            for (String line:tempData)
                tempString += line;
            //remove head
            tempString = tempString.replace("{\"crs\": {\"type\": \"name\", \"properties\": {\"name\": \"urn:ogc:def:crs:EPSG::4326\"}}, \"type\": \"FeatureCollection\", \"features\": [{\"geometry\": {\"type\": \"Polygon\", \"coordinates\": [[[","");
            //remove end
            tempString = tempString.replace("}}]}","");
            tempSplit = tempString.split("}}, \\{\"geometry\": \\{\"type\": \"Polygon\", \"coordinates\": \\[\\[\\[|]]]}, \"type\": \"Feature\", \"id\": [0-9]+, \"properties\": \\{\"cellId\": ");
            HashMap<String,HashSet<String>> tempMap = new HashMap<>();
            HashSet<String> tempSet;
            for (int i = 0;i * 2 < tempSplit.length;i ++){
                tempSet = new HashSet(Arrays.asList(tempSplit[i * 2].split("], \\[")));
                tempMap.put(tempSplit[i * 2 + 1],tempSet);
            }
            for (Map.Entry<String,HashSet<String>> i:tempMap.entrySet())
                for (Map.Entry<String,HashSet<String>> j:tempMap.entrySet())
                    if (!neighborSet.contains(i.getKey() + "\t:\t" + j.getKey()) && !i.equals(j)) {
                        for (String s:j.getValue())
                            if (i.getValue().contains(s)) {
                                neighborSet.add(i.getKey() + "\t:\t" + j.getKey());
                                break;
                            }
                    }
        }
        else {
            //first row and first column represent the cell id
            String[] firstRow = tempData.get(0).split("\t");
            String[] firstColumn = new String[tempData.size()];
            int count = 0;
            for (String s:tempData) {
                firstColumn[count] = s.split("\t")[0];
                count ++;
            }
            for (int i = 1; i < tempData.size();i ++) {
                tempSplit = tempData.get(i).split("\t");
                for (int j = 1; j < tempSplit.length;j ++)
                    if (tempSplit[j].equals("1") || tempSplit[j].equals("true"))
                        neighborSet.add(firstRow[j] + "\t:\t" + firstColumn[i]);
            }
        }
        return neighborSet;
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

    //obsolete isMerging
    /*private boolean isMerging(double[] capacities) {
        Iterator<SuperCellPair<Double>> iterator = this.interactions.descendingIterator();
        SuperCellPair<Double> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            //assume equal capacities, only capacities[0] is considered
            if (!entry.pair.id1.equals(entry.pair.id2) && this.neighborSet.contains(new Pair<>(entry.pair.id1, entry.pair.id2))) {
                if (entry.ij + entry.ii + entry.jj <= capacities[0]) {
                    Encapsulation encapsulation = this.merge(entry);
                    this.interactions = encapsulation.treeSet;
                    this.neighborSet = encapsulation.hashSet;
                    return true;
                }
            }
        }
        return false;
    }*/

    private boolean isMerging(double capacity) {
        Iterator<SuperCellPair<Double>> iterator = this.interactions.descendingIterator();
        SuperCellPair<Double> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (!entry.pair.id1.equals(entry.pair.id2) && this.neighborSet.contains(new Pair<>(entry.pair.id1, entry.pair.id2)) && entry.ij + entry.ii + entry.jj <= capacity) {
                Encapsulation encapsulation = this.merge(entry);
                this.interactions = encapsulation.treeSet;
                this.neighborSet = encapsulation.hashSet;
                return true;
            }
        }
        return false;
    }

    private Encapsulation merge(SuperCellPair<Double> tobeMerged) {
        String mergedId = tobeMerged.pair.id1 + "," + tobeMerged.pair.id2;
        double mergedValue = tobeMerged.ij + tobeMerged.ii + tobeMerged.jj;
        //update TreeSet
        TreeSet<SuperCellPair<Double>> interactionSet = new TreeSet<>(new SuperCellPairComparator0());
        //add merged super cell
        interactionSet.add(new SuperCellPair(mergedId,mergedId,mergedValue,mergedValue,mergedValue));
        //because treeset can not be used to look for objects to be updated, temporary map is used to store updating objects to be added into treeset later
        HashMap<String,SuperCellPair<Double>> tempMap = new HashMap<>();
        SuperCellPair tempEntry;
        for(SuperCellPair<Double> entry:this.interactions) {
            //if entry has none in the merging
            if(!tobeMerged.pair.contains(entry.pair.id1) && !tobeMerged.pair.contains(entry.pair.id2)) {
                interactionSet.add(entry);
                continue;
            }
            //if entry id1 is in the merging, and not id2
            if (tobeMerged.pair.contains(entry.pair.id1) && !tobeMerged.pair.contains(entry.pair.id2)) {
                tempEntry = new SuperCellPair<>(mergedId,entry.pair.id2,entry.ij,mergedValue,entry.jj);
                tempMap.merge(entry.pair.id2,tempEntry,(SuperCellPair<Double> oldEntry,SuperCellPair<Double> newEntry) -> {
                    newEntry = new SuperCellPair<>(newEntry.pair.id1,newEntry.pair.id2,oldEntry.ij + newEntry.ij,newEntry.ii,newEntry.jj);
                    return newEntry;
                });
                continue;
            }
            //if entry id2 is in the merging, and not id1
            if (!tobeMerged.pair.contains(entry.pair.id1) && tobeMerged.pair.contains(entry.pair.id2)) {
                tempEntry = new SuperCellPair(entry.pair.id1,mergedId,entry.ij,entry.ii,mergedValue);
                tempMap.merge(entry.pair.id1,tempEntry,(SuperCellPair<Double> oldEntry,SuperCellPair<Double> newEntry) -> {
                    newEntry = new SuperCellPair(newEntry.pair.id1,newEntry.pair.id2,oldEntry.ij + newEntry.ij,newEntry.ii,newEntry.jj);
                    return newEntry;
                });
                continue;
            }
        }
        interactionSet.addAll(tempMap.values());
        //update HashSet
        HashSet<Pair<String>> neighborSet = new HashSet<>();
        for(Pair<String> pair:this.neighborSet) {
            if(tobeMerged.pair.contains(pair.id1))
                pair.id1 = mergedId;
            if(tobeMerged.pair.contains(pair.id2))
                pair.id2 = mergedId;
            neighborSet.add(pair);
        }
        neighborSet.remove(new Pair<>(mergedId,mergedId));
        return new Encapsulation(interactionSet,neighborSet);
    }

    @Override
    public HashSet<String>[] partition(double capacity,int serverNumber){
        while(this.isMerging(capacity));
        //initialize partition for return
        HashSet<String>[] partitions = new HashSet[serverNumber];
        Arrays.fill(partitions,new HashSet<>());
        int index = 0;
        //iterate over partitions
        Iterator<SuperCellPair<Double>> iterator = this.interactions.descendingIterator();
        SuperCellPair<Double> interactionEntry;
        while(iterator.hasNext() && index < serverNumber) {
            interactionEntry = iterator.next();
            if (interactionEntry.pair.id1.equals(interactionEntry.pair.id2)) {
                partitions[index] = new HashSet<>(Arrays.asList(interactionEntry.pair.id1.split(",")));
                index ++;
            }
        }
        return partitions;
    }

    /*//@Override
    public void partition(double capacity){
        while(this.isMerging(capacity));
    }

    //@Override
    public HashSet<String>[] assign(double capacity,int serverNumber){
        //save original interactions
        HashSet originalInteraction = new HashSet(this.interactions);
        //while(this.isMerging(capacity));
        //initialize partition for return
        HashSet<String>[] partitions = new HashSet[serverNumber];
        Arrays.fill(partitions,new HashSet<>());
        int index = 0;
        //iterate over partitions
        Iterator<SuperCellPair<Double>> iterator = this.interactions.descendingIterator();
        SuperCellPair<Double> interactionEntry;
        while(iterator.hasNext() && index < serverNumber) {
            interactionEntry = iterator.next();
            if (interactionEntry.pair.id1.equals(interactionEntry.pair.id2)) {
                partitions[index] = new HashSet<>(Arrays.asList(interactionEntry.pair.id1.split(",")));
                index ++;
            }
        }
        //load original interactions
        this.interactions = new TreeSet<>(new SuperCellPairComparator0());
        this.interactions.addAll(originalInteraction);
        return partitions;
    }*/

    /*@Override
    public HashSet<String>[] partition(double[] capacities) {
        //merge interactions
        while(this.isMerging(capacities));
        //sort servers by capacities
        HashMap<Integer,Double> tempServers = new HashMap<>();
        for(int i = 0;i < capacities.length;i ++)
            tempServers.put(i,capacities[i]);
        TreeSet<Map.Entry<Integer,Double>> sortedServers = new TreeSet<>(new ValueComparator<>());
        sortedServers.addAll(tempServers.entrySet());
        Iterator<SuperCellPair<Double>> iterator = this.interactions.descendingIterator();
        //initialize partition for return
        HashSet<String>[] partitions = new HashSet[capacities.length];
        Arrays.fill(partitions,new HashSet<>());
        SuperCellPair<Double> interactionEntry;
        for(Map.Entry<Integer,Double> entry:sortedServers)
            while(iterator.hasNext()) {
                interactionEntry = iterator.next();
                if (interactionEntry.pair.id1.equals(interactionEntry.pair.id2)) {
                    partitions[entry.getKey()] = new HashSet<>(Arrays.asList(interactionEntry.pair.id1.split(",")));
                    break;
                }
            }
        return partitions;
    }*/

    private class Encapsulation {

        private TreeSet<SuperCellPair<Double>> treeSet;
        private HashSet<Pair<String>> hashSet;

        private Encapsulation(TreeSet<SuperCellPair<Double>> treeSet,HashSet<Pair<String>> hashSet) {
            this.treeSet = treeSet;
            this.hashSet = hashSet;
        }

    }

}