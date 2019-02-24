/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endofdaybacktester;

import java.awt.Color;
import java.io.*;
import java.util.*;

class Data
{
    public long date;
    public double open;
    public double high;
    public double low;
    public double close;   
}

class Chart
{
    public int length;
    public long[] date;
    public double[] open;
    public double[] high;
    public double[] low;
    public double[] close;
    
    public String GetRecord(int id)
    {
        Calendar date = Calendar.getInstance();
        date.clear();        
        date.setTimeInMillis(this.date[id]);
        String string = date.getTime().toString() + " " +
                open[id] + " " +
                high[id] + " " +
                low[id] + " " +
                close[id];
        
        return string;
    }
}


class Account {
    
    public double balance;
    public double chg_balance;
    public double equity;
    public ArrayList inventory;
    public int total_quantity;
    public double total_cost;
    public double average_cost;
    public double market_price;
    public double market_value;
    public double commission;
    public double profit;
    
    public Account()
    {
        balance = 100000;
        equity = balance;
        inventory = new ArrayList();
        commission = 2;
        
    }
    
    
    public void buy(int quantity, double price)
    {
        chg_balance = 0;
        
        int current_qty = 0;
        
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            int qty = (int)arr[0];
            current_qty += qty;
        }
        
        if (current_qty < 0)
        {
            double profit = 0;
            int counted_qty = 0;
            
            while (true)
            {
                Object[] arr = (Object[])inventory.get(0);

                int qty = (int)arr[0];
                double prc = (double)arr[1];

                if (counted_qty + qty >= -quantity)
                {
                    profit -= qty * (prc - price);
                    
                    inventory.remove(0);

                    counted_qty += qty;

                    if (inventory.size() == 0)
                    {
                        qty = counted_qty + quantity;
                        if (qty > 0)
                        {
                            inventory.add(new Object[]{qty, price});
                        }
                        break;
                    }

                }
                else
                {
                    profit -= (-quantity - counted_qty) * (prc - price);
                    qty -= (-quantity - counted_qty);
                    arr[0] = qty;
                    inventory.set(0, arr);
                    break;
                }
            }   
            
            balance += profit - commission;
            chg_balance = profit - commission;
        
        }
        else
        {
            inventory.add(new Object[]{quantity, price});
            
        }
        
        Update(price);
        
    }
    
    
    
    public void sell(int quantity, double price)
    {
        chg_balance = 0;
                
        int current_qty = 0;
        
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            int qty = (int)arr[0];
            current_qty += qty;
        }
        
        if (current_qty > 0)
        {
            double profit = 0;
        
            int counted_qty = 0;

            while (true)
            {
                Object[] arr = (Object[])inventory.get(0);

                int qty = (int)arr[0];
                double prc = (double)arr[1];
                
                if (counted_qty + qty <= quantity)
                {
                    profit += qty * (price - prc);
                    
                    inventory.remove(0);                    
                    
                    counted_qty += qty;

                    if (inventory.size() == 0)
                    {
                        qty = counted_qty - quantity;
                        if (qty < 0)
                        {
                            inventory.add(new Object[]{qty, price});
                        }
                        break;
                    }

                }
                else
                {
                    profit += (quantity - counted_qty) * (price - prc);
                    qty -= (quantity - counted_qty);
                    arr[0] = qty;
                    inventory.set(0, arr);
                    break;
                }
            }
                        
            balance += profit - commission;
            chg_balance = profit - commission;
        
        }
        else
        {
            inventory.add(new Object[]{-quantity, price});
        }
                
        Update(price);
        
        
    }
    
    
    
    
    
    public double CalculateTotalCost()
    {
        double result = 0;
        
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            
            int qty = (int)arr[0];
            double price = (double)arr[1];
            
            result += qty * price;
        }
        
        
        return result;
    }
    
    public int CalculateTotalQuantity()
    {
        int result = 0;
        
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            
            int qty = (int)arr[0];
            
            result += qty;
        }
        
        
        return result;
        
    }
    
    public double CalculateAverageCost()
    {
        double result = 0;
        
        if (total_quantity != 0)
        {        
            result = total_cost / total_quantity;
        }
        
        
        return result;
    }
    
    public double CalculateProfit()
    {
        double result = 0;
        
        
        if (total_quantity > 0)
        {
            result = (market_price - average_cost) * Math.abs(total_quantity);
        }
        else if (total_quantity < 0)
        {
            result = (average_cost - market_price) * Math.abs(total_quantity);
        }
        
        if (total_quantity != 0)
        {
            result -= commission;
        }
        
        return result;
    }
    
    
    public double CalculateMarketValue()
    {
        double result = 0;
        
        result = total_quantity * market_price;
        
        return result;
    }
    
    public double CalculateCommission()
    {
        return commission * market_price;
    }
    
    public void Update(double price)
    {
        market_price = price;
        total_quantity = CalculateTotalQuantity();
        total_cost = CalculateTotalCost();
        average_cost = CalculateAverageCost();
        market_value = CalculateMarketValue();
        profit = CalculateProfit();
        
        equity = balance + profit;
    }
    
    public void PrintInventory()
    {
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            System.out.println(arr[0] + "  " + arr[1]);
        }
    }
    
    public void PrintSummary()
    {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        String string = "";
        
        string += df.format(balance) + " ";
        string += df.format(equity);
        
        System.out.println(string);
    }
    
    public void Print()
    {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        String string = "";
        
        string += "tqty: " + total_quantity + " ";
        string += "cost: " + df.format(total_cost) + " ";
        string += "mktv: " + df.format(market_value) + " ";
        string += "bal: " + df.format(balance) + " ";
        string += "eq: " + df.format(equity) + " ";
        string += "pr: " + df.format(profit);
        
        System.out.println(string);
        
    }
    
    
}



class Output 
{
    public int beg_id;
    public double[] values;
    public Color color;
    
}

class ExponentialMovingAverage 
{
    public Chart owner;
    public Output line;
    public int length;    
    
    public void Calculate(int id)
    {
        try
        {
            
        
            
            double value = 0;

            double val = owner.close[id];

            if (id == length - 1) 
            {
                value = val;
            }
            else if (id > length - 1)
            {
                value = val * (2.0 / (1 + length)) + (1 - (2.0 / (1 + length))) * line.values[id - 1];
            }

            line.values[id] = value;
            
            if (id >= length - 1 && line.beg_id == -1) line.beg_id = id;

            
        
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
            
    }
}


class TradingSystem
{
    public ExponentialMovingAverage exp;
    public Chart chart;
    public Account account;
    
    public TradingSystem()
    {
        
        chart = new Chart();

        chart.length = 20;
        chart.date = new long[chart.length];
        chart.open = new double[chart.length];
        chart.high = new double[chart.length];
        chart.low = new double[chart.length];
        chart.close = new double[chart.length];

        exp = new ExponentialMovingAverage();
        exp.length = 5;
        exp.owner = chart;
        exp.line = new Output();
        exp.line.beg_id = -1;
        exp.line.values = new double[chart.length];
        
        account = new Account();
        
    }
    
    public void evaluate_entry(int id)
    {
        if (chart.close[id] > exp.line.values[id] &&
                chart.close[id - 1] <= exp.line.values[id])
        {
            int qty = (int)(account.equity / chart.close[id]);
            
            account.buy(qty, chart.close[id]);

            Calendar date = Calendar.getInstance();
            date.clear();
            date.setTimeInMillis(chart.date[id]);
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
            String string = "";
        
            string += date.getTime() + " buy open " + qty + " " + chart.close[id] + " ";
            string += df.format(account.balance) + " ";
            string += df.format(account.equity);
        
            System.out.println(string);

            
        }                            
                           
    }
    
    public void update(int id)
    {
        account.Update(chart.close[id]);
    }
    
    public void evaluate_exit(int id)
    {
        if (account.profit > 100)
        {
            int qty = account.total_quantity;
                        
            account.sell(account.total_quantity, account.market_price);
            
            Calendar date = Calendar.getInstance();
            date.clear();
            date.setTimeInMillis(chart.date[id]);
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
            String string = "";
        
            string += date.getTime() + " sell close " + qty + " " + chart.close[id] + " ";
            string += df.format(account.balance) + " ";
            string += df.format(account.equity);
        
            System.out.println(string);

            
        }
        
        
        else if (account.profit < -4000)
        {
            int qty = account.total_quantity;
            
            if (qty / 7 > 10)
            {
                qty /= 7;
            }
            else
            {
                qty = 10;
            }
            
            account.sell(qty, account.market_price);

            
            Calendar date = Calendar.getInstance();
            date.clear();
            date.setTimeInMillis(chart.date[id]);
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
            String string = "";
        
            string += date.getTime() + " sell partial " + qty + " " + chart.close[id] + " ";
            string += df.format(account.balance) + " ";
            string += df.format(account.equity);
        
            System.out.println(string);

            
        }
        
    }
    
}


/**
 *
 * @author Russell Brown
 */
public class EndOfDayBacktester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        read_file();
        
    }
    
    public static void test1()
    {
         Account account = new Account();
        
        account.buy(100, 78);
        account.Print();
        
        account.Update(82);       
        
        account.Print();
        
        account.sell(account.CalculateTotalQuantity(), account.market_price);
        
        account.Print();
        
    }
    
    public static void test2()
    {
          Account account = new Account();
        
        account.sell(100, 38);
        account.sell(100, 48);
        account.sell(100, 58);
        account.sell(100, 78);
        account.Print();
        
        account.Update(82);
                
        account.Print();
        
        
        account.buy(420, account.market_price);
        
        account.Print();
        
        account.Update(86);
        account.Print();
    }
    
    
    public static void test3()
    {
          
        Account account = new Account();
        
        account.buy(100, 38);
        account.buy(100, 48);
        account.buy(100, 58);
        account.buy(100, 78);
        account.Print();
        
        account.Update(82);
                
        account.Print();
        
        
        account.sell(420, account.market_price);
        
        account.Print();
        
        account.Update(70);
        account.Print();
        
//        account.PrintInventory();
        
        
    }
    
    
    
    public static void read_file()
    {
        
        
        
        // Source:   investing.com
        
        
        try
        {
            String path = "C:\\Users\\Russell Brown\\Documents\\td_data.csv";
            
            RandomAccessFile f = new RandomAccessFile(path, "r");
            
            f.getChannel().position(f.getChannel().size() - 1);
            
            long prev_pos = f.getChannel().position();
            
            byte[] data = new byte[1000];
            
            boolean end_of_file = false;
            
            long length = prev_pos - data.length;
            
            if (length < 0)
            {
                if (data.length >= f.getChannel().size())
                {
                    data = new byte[(int)f.getChannel().size() - 1];
                    f.getChannel().position(0);
                    end_of_file = true;
                }
            }
            else
            {
                f.getChannel().position(length);
            }
            
            Calendar date = Calendar.getInstance();
            date.clear();                        
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MMM-yy");
            
            Data price_data = new Data();
            
            
            TradingSystem ts = new TradingSystem();
            
            
            int id = 0;

            if (end_of_file == false)
            {

                while (true)
                {

                    prev_pos = f.getChannel().position();

                    f.read(data);                        

                    int last_pos = data.length - 1;

                    for (int i = data.length-1; i >= 0; i--)
                    {                
                       if (data[i] == '\n')
                        {
                            if (end_of_file == true)
                            {
                                prev_pos = i;
                            }

                            int c = 0;

                            String text = "";

                            for (int i2 = i+1; i2 < last_pos; i2++)
                            {
                                if ((char)data[i2] == ',')
                                {                          
                                    if (c == 0)
                                    {
                                        date.setTime(df.parse(text));
                                        price_data.date = date.getTimeInMillis();
                                    }
                                    else if (c == 1)
                                    {
                                        price_data.close = Double.valueOf(text);
                                    }
                                    else if (c == 2)
                                    {
                                        price_data.open = Double.valueOf(text);
                                    }
                                    else if (c == 3)
                                    {
                                        price_data.high = Double.valueOf(text);
                                    }
                                    else if (c == 4)
                                    {
                                        price_data.low = Double.valueOf(text);
                                    }
                                    c++;
                                    text = "";
                                }
                                else
                                {
                                    text += (char)data[i2];
                                }

                            }
                            
                            if (id < ts.chart.length)
                            {

                                ts.chart.date[id] = price_data.date;
                                ts.chart.open[id] = price_data.open;
                                ts.chart.high[id] = price_data.high;
                                ts.chart.low[id] = price_data.low;
                                ts.chart.close[id] = price_data.close;
                                                       
                                id++;
                            
                            }
                            else
                            {
                                for (int ci = 0; ci < ts.chart.length - 1; ci++)
                                {
                                    ts.chart.date[ci] = ts.chart.date[ci + 1];
                                    ts.chart.open[ci] = ts.chart.open[ci + 1];
                                    ts.chart.high[ci] = ts.chart.high[ci + 1];
                                    ts.chart.low[ci] = ts.chart.low[ci + 1];
                                    ts.chart.close[ci] = ts.chart.close[ci + 1];
                                }
                                
                                ts.chart.date[ts.chart.length - 1] = price_data.date;
                                ts.chart.open[ts.chart.length - 1] = price_data.open;
                                ts.chart.high[ts.chart.length - 1] = price_data.high;
                                ts.chart.low[ts.chart.length - 1] = price_data.low;
                                ts.chart.close[ts.chart.length - 1] = price_data.close;
                                
                               
                            }
                            
                            
                            for (int i2 = 0; i2 < ts.chart.length; i2++)
                            {
                                ts.exp.Calculate(i2);
                            }

                            ts.update(id - 1);
                            
                            if (id >= ts.exp.line.beg_id)
                            {

                                ts.evaluate_entry(id - 1);
                            
                            }

                            ts.update(id - 1);
                            ts.evaluate_exit(id - 1);

                            
                            
//                            String string = ts.chart.GetRecord(id - 1);
//                            
//                            System.out.println(string + "  " + ts.exp.line.values[id - 1]);
                            
                            

//                            System.out.println(price_data.date.getTime() + " " +
//                                    price_data.open + " " +
//                                    price_data.high + " " +
//                                    price_data.low + " " +
//                                    price_data.close);

                            price_data = new Data();


                            last_pos = i;
                        }

                    }            

                    if (end_of_file == true)
                    {

                        break;
                    }

                    long pos2 = prev_pos - data.length + last_pos;

                    if (pos2 < 0)
                    {                   
                        prev_pos += last_pos;
                        break;
                    }

                    f.getChannel().position(pos2);

                }
                
                
                data = new byte[(int)prev_pos];
            
            }


            f.getChannel().position(0);
            f.read(data);

            

            int last_pos = data.length;



            for (int i = data.length - 1; i >= -1; i--)
            {
                
                if ((i >= 0 && data[i] == '\n') || i == -1)
                {
                    int c = 0;
                    String text = "";
                    for (int i2 = i+1; i2 < last_pos; i2++)
                    {
                        if ((char)data[i2] == ',')
                        {                     
                            if (c == 0)
                            {
                                date.setTime(df.parse(text));
                                price_data.date = date.getTimeInMillis();
                            }
                            else if (c == 1)
                            {
                                price_data.close = Double.valueOf(text);
                            }
                            else if (c == 2)
                            {
                                price_data.open = Double.valueOf(text);
                            }
                            else if (c == 3)
                            {
                                price_data.high = Double.valueOf(text);
                            }
                            else if (c == 4)
                            {
                                price_data.low = Double.valueOf(text);
                            }
                            c++;
                            text = "";
                        }
                        else
                        {
                            text += (char)data[i2];
                        }

                    }
                    
                    if (id < ts.chart.length)
                    {

                        ts.chart.date[id] = price_data.date;
                        ts.chart.open[id] = price_data.open;
                        ts.chart.high[id] = price_data.high;
                        ts.chart.low[id] = price_data.low;
                        ts.chart.close[id] = price_data.close;

                        id++;

                    }
                    else
                    {
                        for (int ci = 0; ci < ts.chart.length - 1; ci++)
                        {
                            ts.chart.date[ci] = ts.chart.date[ci + 1];
                            ts.chart.open[ci] = ts.chart.open[ci + 1];
                            ts.chart.high[ci] = ts.chart.high[ci + 1];
                            ts.chart.low[ci] = ts.chart.low[ci + 1];
                            ts.chart.close[ci] = ts.chart.close[ci + 1];
                        }

                        ts.chart.date[ts.chart.length - 1] = price_data.date;
                        ts.chart.open[ts.chart.length - 1] = price_data.open;
                        ts.chart.high[ts.chart.length - 1] = price_data.high;
                        ts.chart.low[ts.chart.length - 1] = price_data.low;
                        ts.chart.close[ts.chart.length - 1] = price_data.close;
                    }
                    


                    for (int i2 = 0; i2 < ts.chart.length; i2++)
                    {
                        ts.exp.Calculate(i2);
                    }

               
                    ts.update(id - 1);
                    
                    if (id >= ts.exp.line.beg_id)
                    {

                        ts.evaluate_entry(id - 1);

                    }
                    
                    ts.update(id - 1);
                    ts.evaluate_exit(id - 1);
                    
                            

//                    String string = ts.chart.GetRecord(id - 1);
//
//                    System.out.println(string + "  " + ts.exp.line.values[id - 1]);
                            
                    
                    

//                    String string = chart.GetRecord(chart.length - 1);
//
//                    System.out.println(string);


                    

//                    System.out.println(price_data.date.getTime() + " " +
//                                price_data.open + " " +
//                                price_data.high + " " +
//                                price_data.low + " " +
//                                price_data.close);

                    price_data = new Data();


                }
            
            
            
           
            }
            
            
            
            
            
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        
        
        
    }
    
}
