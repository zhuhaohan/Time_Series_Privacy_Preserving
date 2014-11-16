import java.util.LinkedList;
/**
 * @author Haohan Zhu
 * This code is Sequence Class
 */
public class Sequence<T> {
    
    private int dimension;
    private LinkedList<LinkedList<T>> sequence;
    
    public Sequence(){
        this.dimension = 0;
        this.sequence = new LinkedList<LinkedList<T>>();
    }
    
    public Sequence(LinkedList<LinkedList<T>> sequence){
        this.sequence = sequence;
        this.dimension = sequence.getFirst().size();
    }
    
    public int getLength(){
        return this.sequence.size();
    }
    
    public int getDimension(){
        return this.dimension;
    }
    
    public LinkedList<T> getElement(int index){
        return this.sequence.get(index);
    }
    
    public boolean insertElement(LinkedList<T> element){
        if (this.dimension==0){
           this.sequence.add(element);
           this.dimension = element.size();
           return true;
        }
        else if (dimension == element.size()){
            this.sequence.add(element);
            return true;
        }
        else{
            return false;
        }
    }
    
    @Override
    public String toString(){
        String s = "";
        for(int i=0; i<this.sequence.size(); i++){
            s += this.getElement(i)+ ",";
        }
        return s.substring(0, s.length()-1);
    }
}
