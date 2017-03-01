import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class TxHandler {
	/**save unspent txs's output***/
	UTXOPool utxoPool;
	/**mapping tx's hash to it self**/
//	private HashMap<String,Transaction> mapping=new HashMap<String,Transaction>();
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
    	if(utxoPool!=null){
    		this.utxoPool=new UTXOPool(utxoPool);
    	}else{
    		this.utxoPool=new UTXOPool();
    	}
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
    	
        // IMPLEMENT THIS
    	double inVal=0d,outVal=0d;
    	ArrayList<Transaction.Input> inputs=tx.getInputs();
    	ArrayList<Transaction.Output> outputs=tx.getOutputs();
    	if(inputs==null||outputs==null){//there is no input or output
    		return true;
    	}
    	//all unspent transactions
    	ArrayList<UTXO> utxos=this.utxoPool.getAllUTXO();
    	for(int index=0;index<inputs.size();index++){
    		Transaction.Input input=inputs.get(index);
    		UTXO in=new UTXO(input.prevTxHash, input.outputIndex);
    		Transaction.Output output=null;
    		for(UTXO utxo:utxos){
        		if(utxo.equals(in)){
        			output=this.utxoPool.getTxOutput(utxo);
        			if(!Crypto.verifySignature(output.address,tx.getRawDataToSign(index),input.signature)){
        				return false;
        			}
        			inVal+=output.value;
        		}
        	}
    		if(output==null){// input not in current UTXO pool
    			return false;
    		}
    	}
    	
    	
    	//no UTXO is claimed multiple times by {@code tx},
    	if(haveRepeatInput(tx)){
    		return false;
    	}
    	//all output'value is greater than zero
    	for(Transaction.Output out:outputs){
    		if(out.value<0){
    			return false;
    		}
    		outVal+=out.value;
    	}
    	
    	if(inVal<outVal){//in less than out
    		return false;
    	}
    	
		return true;
    }
    
    /**
     * get Transaction of input's prevTxHash
     */
    
    private boolean haveRepeatInput(Transaction tx){
    	Map map=new HashMap();
    	//get all input
    	ArrayList<Transaction.Input> ins= tx.getInputs();
    	for(int i=0,len=ins.size();i<len;i++){
    		Transaction.Input in1=ins.get(i);
    		UTXO u1=new UTXO(in1.prevTxHash	, in1.outputIndex);
    		int code=u1.hashCode();
    		if(map.get(code)==null){
    			map.put(code, code);
    		}else{
    			return true;
    		}
    		
    	}
		return false;
    }
    
    

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
    	ArrayList<Transaction> validedTarnsactions=new ArrayList<Transaction>();
    	if(possibleTxs==null||possibleTxs.length==0){
    		return null;
    	}
        for(Transaction tx:possibleTxs){
        	boolean valid=this.isValidTx(tx);
        	if(valid){
        		validedTarnsactions.add(tx);
        		updateUTXO(tx);
        	}
        }
        Transaction[] ret=new Transaction[validedTarnsactions.size()];
        for(int i=0;i<validedTarnsactions.size();i++){
        	ret[i]=validedTarnsactions.get(i);
        }
        return ret;
    }
    
	private void updateUTXO(Transaction tx){
    	ArrayList<Transaction.Input> inputs=tx.getInputs();
    	ArrayList<Transaction.Output> outputs=tx.getOutputs();
    	ArrayList<UTXO> utxos=this.utxoPool.getAllUTXO();
    	for(Transaction.Input input:inputs){
    		UTXO tmp=new UTXO(input.prevTxHash, input.outputIndex);
    		for(UTXO utxo:utxos){
        		if(utxo.equals(tmp)){
        			this.utxoPool.removeUTXO(utxo);
        			break;
        		}
    		}
    		
    	}
    	for(int index=0;index<outputs.size();index++){
    		Transaction.Output output=outputs.get(index);
    		UTXO utxo=new UTXO(tx.getHash(), index);
    		this.utxoPool.addUTXO(utxo, output);
    	}
    	
    }

    
}
