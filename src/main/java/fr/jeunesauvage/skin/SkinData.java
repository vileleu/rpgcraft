package fr.jeunesauvage.skin;

public class SkinData {
	private	final String signature;
	private	final String value;

	SkinData(String signature, String value) {
		this.signature = signature;
		this.value = value;
	}

	public String getSignature() {
		return signature;
	}

	public String getValue() {
		return value;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinData skinData)) return false;
        return java.util.Objects.equals(signature, skinData.signature) && java.util.Objects.equals(value, skinData.value);
    }
}
