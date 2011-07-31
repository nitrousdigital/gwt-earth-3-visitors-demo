package com.nitrous.gwtearth.visitors.client;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.nitrous.gwtearth.visitors.shared.CountryMetric;

public class MetricTable extends Composite {
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("MMM-dd-yyyy");
	private ListDataProvider<CountryMetric> dataProvider;
	private CellTable<CountryMetric> table;
	private SingleSelectionModel<CountryMetric> selectionModel;
	private SelectionListener selectionListener;
	public MetricTable() {
		table = new CellTable<CountryMetric>();
		selectionModel = new SingleSelectionModel<CountryMetric>();
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				if (selectionListener != null) {
					CountryMetric metric = selectionModel.getSelectedObject();
					selectionListener.onSelected(metric);
				}
			}
		});
		table.setSelectionModel(selectionModel);
		
		// Country column
		TextColumn<CountryMetric> countryColumn = new TextColumn<CountryMetric>() {
			@Override
			public String getValue(CountryMetric metric) {
				return metric.getCountry();
			}
		};
		countryColumn.setSortable(true);

		// Visit count column
		TextColumn<CountryMetric> visitCountColumn = new TextColumn<CountryMetric>() {
			@Override
			public String getValue(CountryMetric metric) {
				return String.valueOf(metric.getVisitCount());
			}
		};
		visitCountColumn.setSortable(true);

		// Last Visit Date column
		TextColumn<CountryMetric> visitDateColumn = new TextColumn<CountryMetric>() {
			@Override
			public String getValue(CountryMetric metric) {
				return dateFormat.format(metric.getLastVisitDate());
			}
		};
		visitDateColumn.setSortable(true);

		// add the columns
		table.addColumn(countryColumn, "Country");
		table.addColumn(visitCountColumn, "Visits");
		table.addColumn(visitDateColumn, "Last Visit Date");

		// create a data provider and connect to the table
		dataProvider = new ListDataProvider<CountryMetric>();
		dataProvider.addDataDisplay(table);

		// Add column sort handlers
		// Support sorting by country
		ListHandler<CountryMetric> countrySortHandler = new ListHandler<CountryMetric>(dataProvider.getList());
		countrySortHandler.setComparator(countryColumn,
			new Comparator<CountryMetric>() {
				public int compare(CountryMetric o1, CountryMetric o2) {
					if (o1 == o2) {
						return 0;
					}
					if (o1 != null) {
						return (o2 != null) ? o1.getCountry().compareTo(o2.getCountry()) : 1;
					}
					return -1;
				}
			});
		table.addColumnSortHandler(countrySortHandler);

		// Support sorting by visit counts
		ListHandler<CountryMetric> visitCountSortHandler = new ListHandler<CountryMetric>(dataProvider.getList());
		visitCountSortHandler.setComparator(visitCountColumn,
			new Comparator<CountryMetric>() {
				public int compare(CountryMetric o1, CountryMetric o2) {
					if (o1 == o2) {
						return 0;
					}
					
					if (o1 != null) {
						if (o2 != null) {
							Integer o1count = o1.getVisitCount();
							Integer o2count = o2.getVisitCount();
							return o1count.compareTo(o2count);
						} else {
							return 1;
						}
					}
					return -1;
				}
			});
		table.addColumnSortHandler(visitCountSortHandler);
		
		// Support sorting by last visit date
		ListHandler<CountryMetric> lastVisitDateSortHandler = new ListHandler<CountryMetric>(dataProvider.getList());
		lastVisitDateSortHandler.setComparator(visitDateColumn,
			new Comparator<CountryMetric>() {
				public int compare(CountryMetric o1, CountryMetric o2) {
					if (o1 == o2) {
						return 0;
					}
					
					if (o1 != null) {
						if (o2 != null) {
							Long o1date = o1.getLastVisitDate().getTime();
							Long o2date = o2.getLastVisitDate().getTime();
							return o1date.compareTo(o2date);
						} else {
							return 1;
						}
					}
					return -1;
				}
			});
		table.addColumnSortHandler(lastVisitDateSortHandler);

		initWidget(table);
		// assume the data is sorted by date
		//table.getColumnSortList().push(visitDateColumn);

	}

	public SelectionListener getSelectionListener() {
		return selectionListener;
	}

	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void showMetrics(Collection<CountryMetric> metrics) {
		// add the data to the data provider with automatically pushes it to the
		// widget
		List<CountryMetric> list = dataProvider.getList();
		for (CountryMetric metric : metrics) {
			list.add(metric);
		}
		table.setVisibleRange(0, list.size());
	}
}
