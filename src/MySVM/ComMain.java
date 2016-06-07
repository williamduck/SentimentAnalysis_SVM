package MySVM;

import java.io.IOException;

import libsvm.svm;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import service.svm_predict;
import service.svm_train;

public class ComMain {
	public static void main01(String[] args) {
		svm_node pa0 = new svm_node();
		pa0.index = 0;
		pa0.value = 10.0;
		svm_node pa1 = new svm_node();
		pa1.index = -1;
		pa1.value = 10.0;
		svm_node pb0 = new svm_node();
		pb0.index = 0;
		pb0.value = -10.0;
		svm_node pb1 = new svm_node();
		pb1.index = 0;
		pb1.value = -10.0;
		svm_node[] pa = {pa0, pa1};
		svm_node[] pb = {pb0, pb1};
		svm_node[][] datas = {pa, pb};
		double[] lables = {1.0, -1.0};
		
		svm_problem problem = new svm_problem();
		problem.l = 2;
		problem.x = datas;
		problem.y = lables;

		svm_parameter param = new svm_parameter();
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.cache_size = 100;
		param.eps = 0.00001;
		param.C = 1;

		System.out.println(svm.svm_check_parameter(problem, param));
		svm_model model = svm.svm_train(problem, param);

		svm_node pc0 = new svm_node();
		pc0.index = 0;
		pc0.value = -0.1;
		svm_node pc1 = new svm_node();
		pc1.index = -1;
		pc1.value = 0.0;
		svm_node[] pc = {pc0, pc1};

		System.out.println(svm.svm_predict(model, pc));
	}

	public static void main(String[] args) throws IOException {
		String[] arg = {"\\trainfile\\train.txt", "\\trainfile\\model.txt"};
		String[] parg = {"\\trainfile\\test.txt", "\\trainfile\\model.txt", "\\trainfile\\out.txt"};
		System.out.println("----------------svm start---------------");
		svm_train t = new svm_train();
		svm_predict p = new svm_predict();
		t.main(arg);
		p.main(parg);
		System.out.println("-------------svm end-------------");
	}

}
