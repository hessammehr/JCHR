package compiler.CHRIntermediateForm.constraints.ud;

public enum StorageInfo {
    /** the constraint is never stored */ 
	NEVER {
    	@Override
    	public boolean mayBeStored() { return false; }
	},
    /** we do not know */  
	MAYBE {
		@Override
		protected boolean isBetterThan(StorageInfo other) { return false; }
		@Override
		public boolean isCompatibleWith(StorageInfo other) { return true; }
	},
    /** The constraint is always stored */ 
	ALWAYS {
		@Override
		public boolean isCompatibleWith(StorageInfo other) {
			return super.isCompatibleWith(other) || other == FINALLY;
		}
    },
    /** The constraint is always stored, but never whilst active */ 
    FINALLY {
    	@Override
    	public boolean isCompatibleWith(StorageInfo other) {
    		return super.isCompatibleWith(other) || other == ALWAYS;
    	}
    	@Override
    	protected boolean isBetterThan(StorageInfo other) {
    		return super.isBetterThan(other) || other == ALWAYS;
    	}
    };
    
    public static StorageInfo getDefault() {
        return MAYBE;
    }
    
    public boolean mayBeStored() {
    	return true;
    }
    
    protected boolean isCompatibleWith(StorageInfo other) {
    	return other == MAYBE || other == this;
    }
    
    protected boolean isBetterThan(StorageInfo other) {
    	if (!isCompatibleWith(other))
    		throw new IllegalStateException(this + " and " + other + " are incompatible");
    	return this != other && other == MAYBE;
    }
}
