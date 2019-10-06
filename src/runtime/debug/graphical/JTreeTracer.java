package runtime.debug.graphical;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import runtime.Constraint;
import runtime.debug.Tracer;

public class JTreeTracer implements Tracer {
	
	private ConstraintTreeModel model;
	private JTree tree;
	int count;
	
	public JTreeTracer() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		activationStack = new TreePath(root);
		model = new ConstraintTreeModel(root);
		tree = new JTree(model);
		tree.setRootVisible(false);
		tree.setLargeModel(true);
		JFrame frame = new JFrame();
		frame.add(new JScrollPane(tree));
		frame.setVisible(true);
	}
	
	public void reactivated(Constraint constraint) {
		activated(constraint);
	}
	
	public void activated(Constraint constraint) {
		ConstraintTreeNode activated = new ConstraintTreeNode(constraint);
		DefaultMutableTreeNode active 
			= (DefaultMutableTreeNode)activationStack.getLastPathComponent();
		
		active.add(activated);
		model.nodeWasAdded(active);
		tree.expandPath(activationStack);
//		tree.scrollPathToVisible(activationStack);
		
		push(activated);
	}
	
	public void fires(String ruleId, int activeIndex, Constraint... constraints) {
		assertActiveConstraint(constraints[activeIndex]);
	}
	
	public void fired(String ruleId, int activeIndex, Constraint... constraints) {
		assertActiveConstraint(constraints[activeIndex]);
		peek();
	}
	
	public void suspended(Constraint constraint) {
		assertActiveConstraint(constraint);
		pop();
	}
	
	public void removed(Constraint constraint) {
		// NOP
	}

	public void stored(Constraint constraint) {
		// NOP
	}

	public void terminated(Constraint constraint) {
		// NOP
	}
	
	private TreePath activationStack;
	
	protected void push(ConstraintTreeNode node) {
		activationStack = activationStack.pathByAddingChild(node);
	}
	
	protected void assertActiveConstraint(Constraint constraint) {
//		assert getActiveConstraint() == constraint;
		if (getActiveConstraint() != constraint)
			throw new IllegalStateException();
	}
	
	protected Constraint getActiveConstraint() {
		Object node = activationStack.getLastPathComponent();
		if (node instanceof ConstraintTreeNode)
			return ((ConstraintTreeNode)node).getConstraint();
		else
			throw new IllegalStateException();
	}
	
	protected void peek() {
		if (!((ConstraintTreeNode)activationStack.getLastPathComponent()).getConstraint().isAlive()) {
			activationStack = activationStack.getParentPath();
			activationStack.getLastPathComponent();
		}
	}
	
	protected void pop() {
		activationStack = activationStack.getParentPath();
		activationStack.getLastPathComponent();
	}
	
	
	
	protected static class ConstraintTreeModel extends DefaultTreeModel {
		private static final long serialVersionUID = 1L;

		public ConstraintTreeModel(TreeNode root) {
			super(root, true);
		}
		
		protected void nodeWasAdded(TreeNode parent) {
			nodesWereInserted(parent, new int[] {parent.getChildCount()-1});
		}
	}
	
	protected static class ConstraintTreeNode extends DefaultMutableTreeNode {
		private static final long serialVersionUID = 1L;
		
		public ConstraintTreeNode(Constraint constraint) {
			super(constraint);
		}
		public Constraint getConstraint() {
			return (Constraint)getUserObject();
		}
	}
}