import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
	boolean[] followees;//
	double p_graph;
	double p_malicious; 
	double p_txDistribution; 
	int numRounds;//
	int currentRound=0;
	Map<Integer,Integer> map=new HashMap<Integer,Integer>();
	Set<Transaction> pendingTransactions=new HashSet<>();
    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
    	this.p_graph=p_graph;
    	this.p_malicious=p_malicious;
    	this.p_txDistribution=p_txDistribution;
    	this.numRounds=numRounds;
    }

    public void setFollowees(boolean[] followees) {
        // IMPLEMENT THIS
    	this.followees=followees;
    }

    //
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
    	for(Transaction tx:pendingTransactions){
    		this.pendingTransactions.add(tx);
    		map.put(tx.id, 0);
    	}
    }

    public Set<Transaction> sendToFollowers() {
        if(currentRound==numRounds){
        	Set<Transaction> ret=new  HashSet<>();
        
        	for(Transaction tx:pendingTransactions){
        		Integer count=map.get(tx.id);
        		if(count==null){
        			count=0;
        		}
        		if(count>=(int)(followees.length*0.20)){
        			ret.add(tx);
        		}
        	}
        	return ret;
        }
    	return pendingTransactions;
    }

    //
    public void receiveFromFollowees(Set<Candidate> candidates) {
    	currentRound++;
        // IMPLEMENT THIS
    	for(Candidate candidate:candidates){
    		Transaction tx=candidate.tx;
    		int sender=candidate.sender;
    		
    		if(!followees[sender]){//
    			continue;
    		}
    		pendingTransactions.add(tx);
    		
    		Integer count=map.get(tx.id);
    		if(count==null){
    			count=0;
    		}
    		count++;
    		map.put(tx.id,count);
    		
    	}
    	
    }
}
