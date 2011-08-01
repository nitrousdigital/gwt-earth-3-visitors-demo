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
import com.nitrous.gwtearth.visitors.shared.CityMetric;

public class MetricTable extends Composite {
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("MMM-dd-yyyy");
	private ListDataProvider<CityMetric> dataProvider;
	private CellTable<CityMetric> table;
	private SingleSelectionModel<CityMetric> selectionModel;
	private SelectionListener selectionListener;
	public MetricTable() {
		table = new CellTable<CityMetric>();
		selectionModel = new SingleSelectionModel<CityMetric>();
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				if (selectionListener != null) {
					CityMetric metric = selectionModel.getSelectedObject();
					selectionListener.onSelected(metric);
				}
			}
		});
		table.setSelectionModel(selectionModel);
		
		// Country column
		TextColumn<CityMetric> countryColumn = new TextColumn<CityMetric>() {
			@Override
			public String getValue(CityMetric metric) {
				return metric.getCountry();
			}
		};
		countryColumn.setSortable(true);

        // City column
        TextColumn<CityMetric> cityColumn = new TextColumn<CityMetric>() {
            @Override
            public String getValue(CityMetric metric) {
                return metric.getCity();
            }
        };
        cityColumn.setSortable(true);
        
		// Visit count column
		TextColumn<CityMetric> visitCountColumn = new TextColumn<CityMetric>() {
			@Override
			public String getValue(CityMetric metric) {
				return String.valueOf(metric.getVisitCount());
			}
		};
		visitCountColumn.setSortable(true);

		// Last Visit Date column
		TextColumn<CityMetric> visitDateColumn = new TextColumn<CityMetric>() {
			@Override
			public String getValue(CityMetric metric) {
				return dateFormat.format(metric.getLastVisitDate());
			}
		};
		visitDateColumn.setSortable(true);

		// add the columns
		table.addColumn(countryColumn, "Country");
        table.addColumn(cityColumn, "City");
		table.addColumn(visitCountColumn, "Visits");
		table.addColumn(visitDateColumn, "Last Visit Date");

		// create a data provider and connect to the table
		dataProvider = new ListDataProvider<CityMetric>();
		dataProvider.addDataDisplay(table);

		// Add column sort handlers
		// Support sorting by country
		ListHandler<CityMetric> countrySortHandler = new ListHandler<CityMetric>(dataProvider.getList());
		countrySortHandler.setComparator(countryColumn,
			new Comparator<CityMetric>() {
				public int compare(CityMetric o1, CityMetric o2) {
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

        // Support sorting by city
        ListHandler<CityMetric> citySortHandler = new ListHandler<CityMetric>(dataProvider.getList());
        citySortHandler.setComparator(cityColumn,
            new Comparator<CityMetric>() {
                public int compare(CityMetric o1, CityMetric o2) {                    
                    if (o1 == o2) {
                        return 0;
                    }
                    if (o1 != null) {
                        return (o2 != null) ? o1.getCity().compareTo(o2.getCity()) : 1;
                    }
                    return -1;
                }
            });
        table.addColumnSortHandler(citySortHandler);
        
		// Support sorting by visit counts
		ListHandler<CityMetric> visitCountSortHandler = new ListHandler<CityMetric>(dataProvider.getList());
		visitCountSortHandler.setComparator(visitCountColumn,
			new Comparator<CityMetric>() {
				public int compare(CityMetric o1, CityMetric o2) {
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
		ListHandler<CityMetric> lastVisitDateSortHandler = new ListHandler<CityMetric>(dataProvider.getList());
		lastVisitDateSortHandler.setComparator(visitDateColumn,
			new Comparator<CityMetric>() {
				public int compare(CityMetric o1, CityMetric o2) {
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
	}

	public void clear() {
		List<CityMetric> list = dataProvider.getList();
		list.clear();
	}
	
	public SelectionListener getSelectionListener() {
		return selectionListener;
	}

	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void showMetrics(Collection<CityMetric> metrics) {
		// add the data to the data provider with automatically pushes it to the
		// widget
		List<CityMetric> list = dataProvider.getList();
		for (CityMetric metric : metrics) {
			list.add(metric);
		}
		table.setVisibleRange(0, list.size());
	}
}
