package at.netcrawler.ui.crawler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


@SuppressWarnings("serial")
public class ColumnChooser extends JPopupMenu {
	
	private final JTable table;
	
	public ColumnChooser(JTable table, Collection<String> columns,
			MouseEvent event) {
		this.table = table;
		
		List<String> visibleColumns = new LinkedList<String>();
		Enumeration<TableColumn> temp = table.getColumnModel().getColumns();
		while (temp.hasMoreElements()) {
			visibleColumns.add((String) temp.nextElement().getHeaderValue());
		}
		
		for (String column : columns) {
			JMenuItem item = createColumnCheckbox(column, visibleColumns
					.contains(column));
			add(item);
		}
		
		addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				hideMenu();
			}
		});
		
		show(table, event.getX(), event.getY());
	}
	
	private void hideMenu() {
		setVisible(false);
	}
	
	private JMenuItem createColumnCheckbox(final String column, boolean selected) {
		JMenuItem item = new JMenuItem(column);
		item.setSelected(selected);
		// TODO: item.setSelectedIcon(selectedIcon);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TableColumnModel columnModel = table.getColumnModel();
				synchronized (columnModel) {
					try {
						columnModel.removeColumn(columnModel
								.getColumn(columnModel.getColumnIndex(column)));
					} catch (IllegalArgumentException e) {
						TableColumn tableColumn = new TableColumn(columnModel
								.getColumnCount());
						tableColumn.setHeaderValue(column);
						columnModel.addColumn(tableColumn);
					}
				}
				
				hideMenu();
			}
		});
		
		return item;
	}
	
}