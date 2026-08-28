package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom;

public interface AttributeCustom {
    int             getValue();
    void            setValue(int value);
    void            increase(int amount);
    void            decrease(int amount);
    int             getValueModifier();
    void            increaseModifier(int amount);
    void            decreaseModifier(int amount);
}
