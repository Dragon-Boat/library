package com.sundae.zl.library;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sundae.zl.mkitemdecoration.AbstractViewModel;
import com.sundae.zl.mkitemdecoration.IHover;
import com.sundae.zl.mkitemdecoration.MKItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	RecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		final Adapter adapter = new Adapter(listData());
		DemoVM demoVM = new DemoVM(this,adapter.data, R.layout.demo_vm_layout);
		recyclerView.addItemDecoration(new MKItemDecoration.Builder()
				.height(50)
				.color(Color.parseColor("#525D97"))
				.textSize(30)
				.textColor(Color.WHITE)
				.itemOffset(0)
				.iHover(new IHover() {
					@Override
					public boolean isGroup(int position) {
						return position % 4 == 0;
					}

					@Override
					public String groupText(int position) {
						return adapter.data.get(4 * (position / 4));
					}
				})
				.viewModel(demoVM)
				.textAlign(MKItemDecoration.Builder.ALIGN_MIDDLE)
				.build());



		recyclerView.setAdapter(adapter);
	}
	private List<String> listData() {
		List<String> data = new ArrayList<>();
		int max = (int) (Math.random() * 20 + 20);
		for (int i = 0; i < max; i++) {
			data.add("item#" + i);
		}
		return data;
	}

	class DemoVM extends AbstractViewModel<String> {

		public DemoVM(Context context, List<String> data, @LayoutRes int resId) {
			super(context, data, resId);
		}

		@Override
		public void bindView(MKItemDecoration.VHolder holder, int position) {
			TextView textView = holder.getView(R.id.demo_vm);
			textView.setText(data.get(4*(position/4)));
//			TextView textView2 = holder.getView(R.id.demo_vm2);
//			textView2.setText(data.get(4 * (position) / 4));
		}
	}
	private class Adapter extends RecyclerView.Adapter {
		List<String> data;

		Adapter(List<String> data) {
			super();
			this.data = data;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new SampleVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample_vh, parent, false));

		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			((SampleVH) holder).onBind(data.get(position));
		}

		@Override
		public int getItemCount() {
			return data.size();
		}
	}
	private class SampleVH extends RecyclerView.ViewHolder {
		TextView textView;

		SampleVH(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.item_tv);
		}

		void onBind(String text) {
			textView.setText(text);
		}
	}
}
