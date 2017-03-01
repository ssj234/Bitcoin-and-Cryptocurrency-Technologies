
/**
 * 描述节点收到的候选交易
 * @author shisj
 *
 */
public class Candidate {
	Transaction tx;
	int sender;
	
	public Candidate(Transaction tx, int sender) {
		this.tx = tx;
		this.sender = sender;
	}
}