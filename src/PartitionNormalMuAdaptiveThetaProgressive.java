import java.util.*;

public class PartitionNormalMuAdaptiveThetaProgressive extends Partition<DistributionNormalMuComparable> {

    private double step = 1;
    private int section = 1;

    //constructor for reading matrix format
    /*public PartitionNormalMuAdaptiveThetaProgressive(String filepath) {
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

    public void setStep(double step){
        this.step = step;
    }

    public void setSection(int section){
        this.section = section;
    }

    private boolean isMerging(double capacity,double theta) {
        Iterator<SuperCellPair<DistributionNormalMuComparable>> iterator = this.interactions.descendingIterator();
        SuperCellPair<DistributionNormalMuComparable> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (!entry.pair.id1.equals(entry.pair.id2) && DistributionNormal.merge(DistributionNormal.merge(entry.ij, entry.ii), entry.jj).cdf(capacity) >= theta) {
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

    public HashSet<String>[] partition(double capacity,int serverNumber){
        /*//save original interactions
        HashSet originalInteraction = new HashSet(this.interactions);*/
        double sectionCapacity;
        for(int i = 1;i <= this.section;i ++) {
            sectionCapacity = capacity * i / this.section;
            double theta = 1d;
            while (this.countPartitions() > serverNumber) {
                while (this.isMerging(sectionCapacity,theta));
                theta -= step;
            }
        }
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
        /*//load original interactions
        this.interactions = new TreeSet<>(new SuperCellPairComparator0());
        this.interactions.addAll(originalInteraction);*/
        return partitions;
    }

}