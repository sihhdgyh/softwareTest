import type { Row } from '../interface'

// 接收一组参数，返回字符串结果
function commissionCaculate(annualSales: number, leaveDays: number, cashArrival: number): string {
    if (annualSales > 200 && leaveDays <= 10 && leaveDays >= 0 && cashArrival <= 100 && cashArrival >= 0) {
        if (cashArrival >= 60)
            return String(annualSales / 7);
        else {
            return 0+"";
        }
    } else if (annualSales >= 0 && leaveDays >= 0 && cashArrival <= 100&& cashArrival >= 0) {
        if (cashArrival <= 85) {
            return annualSales / 6+"";
        } else {
            console.log(annualSales+" "+cashArrival+" "+(annualSales / 5))
            return annualSales / 5+"";
        }
    }
    return 0+"";
}

// 根据.csv文件的表头字段提取对应的参数，返回参数数组
function getArgs(row: Row): number[] {
    let args = [Number.parseInt(row.annualSales as string), Number.parseInt(row.leaveDays as string), Number.parseInt(row.cashArrival as string)]
    return args
}

// 添加别名，进行导出
export { commissionCaculate as useSingleTest, getArgs as useGetArgs }