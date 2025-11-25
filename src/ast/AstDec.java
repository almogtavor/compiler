package ast;

public abstract class AstDec extends AstNode {

    public AstDec() {
        // כל Dec מקבל מספר סידורי
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    @Override
    public void printMe() {
        // מחלקה מופשטת – לא מציירת כלום בעצמה
        // מי שיורש ממנה ידפיס את עצמו
    }
}
