package zpisync.desktop;

public interface IView<TModel extends IModel> {
	void modelToView(TModel model);
	void viewToModel(TModel model);
}
