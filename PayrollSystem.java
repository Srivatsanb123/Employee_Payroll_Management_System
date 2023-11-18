import java.util.StringJoiner;

public class PayrollSystem {
    private TreeNode root;

    public void addEmployee(Employee employee) {
        root = insert(root, employee);
    }

    private TreeNode insert(TreeNode root, Employee employee) {
        if (root == null) {
            return new TreeNode(employee);
        }

        if (employee.getId() < root.employee.getId()) {
            root.left = insert(root.left, employee);
        } else if (employee.getId() > root.employee.getId()) {
            root.right = insert(root.right, employee);
        }

        return root;
    }

    public Employee findEmployee(int employeeId) {
        return findEmployee(root, employeeId);
    }

    private Employee findEmployee(TreeNode root, int employeeId) {
        if (root == null || root.employee.getId() == employeeId) {
            return (root != null) ? root.employee : null;
        }

        if (employeeId < root.employee.getId()) {
            return findEmployee(root.left, employeeId);
        } else {
            return findEmployee(root.right, employeeId);
        }
    }

    public boolean removeEmployee(int employeeId) {
        TreeNode[] result = removeEmployee(root, employeeId);
        if (result != null && result[0] != null) {
            root = result[0];
            return true;
        }
        return false;
    }

    private TreeNode[] removeEmployee(TreeNode root, int employeeId) {
        if (root == null) {
            return new TreeNode[] { null, null };
        }

        if (employeeId < root.employee.getId()) {
            TreeNode[] childResult = removeEmployee(root.left, employeeId);
            root.left = childResult[0];
        } else if (employeeId > root.employee.getId()) {
            TreeNode[] childResult = removeEmployee(root.right, employeeId);
            root.right = childResult[0];
        } else {
            if (root.left == null) {
                return new TreeNode[] { root.right, root };
            } else if (root.right == null) {
                return new TreeNode[] { root.left, root };
            } else {
                TreeNode[] successor = findMin(root.right);
                root.employee = successor[1].employee;
                TreeNode[] childResult = removeEmployee(root.right, successor[1].employee.getId());
                root.right = childResult[0];
            }
        }
        return new TreeNode[] { root, null };
    }

    private TreeNode[] findMin(TreeNode root) {
        if (root.left == null) {
            return new TreeNode[] { root.right, root };
        } else {
            TreeNode[] result = findMin(root.left);
            root.left = result[0];
            return new TreeNode[] { root, result[1] };
        }
    }

    public void updateEmployee(int employeeId, Employee updatedEmployee) {
        removeEmployee(employeeId);
        addEmployee(updatedEmployee);
    }

    public String displayEmployeesToString() {
        StringJoiner joiner = new StringJoiner("\n");
        displayInOrder(root, joiner);
        return joiner.toString();
    }

    private void displayInOrder(TreeNode root, StringJoiner joiner) {
        if (root != null) {
            displayInOrder(root.left, joiner);
            joiner.add(root.employee.toString());
            displayInOrder(root.right, joiner);
        }
    }

    public Employee[] getAllEmployees() {
        Employee[] employees = new Employee[size()];
        fillArrayInOrder(root, employees, 0);
        return employees;
    }

    private int fillArrayInOrder(TreeNode root, Employee[] array, int index) {
        if (root != null) {
            index = fillArrayInOrder(root.left, array, index);
            array[index++] = root.employee;
            index = fillArrayInOrder(root.right, array, index);
        }
        return index;
    }

    private int size() {
        return size(root);
    }

    private int size(TreeNode root) {
        return (root == null) ? 0 : 1 + size(root.left) + size(root.right);
    }

    private static class TreeNode {
        private Employee employee;
        private TreeNode left;
        private TreeNode right;

        public TreeNode(Employee employee) {
            this.employee = employee;
            this.left = null;
            this.right = null;
        }
    }
}
